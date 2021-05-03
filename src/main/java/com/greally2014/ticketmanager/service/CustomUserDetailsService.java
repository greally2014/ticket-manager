package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.RoleRepository;
import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.dto.user.AddressDto;
import com.greally2014.ticketmanager.dto.user.RegistrationDto;
import com.greally2014.ticketmanager.dto.user.UserDto;
import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.other.FileUploadUtil;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.context.annotation.Lazy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String DEFAULT_PROFILE_PICTURE = "default-picture.jpg";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ProjectManagerService projectManagerService;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            @Lazy BCryptPasswordEncoder bCryptPasswordEncoder,
            ProjectManagerService projectManagerService,
            RoleRepository roleRepository,
            UserRepository userRepository
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.projectManagerService = projectManagerService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
        return userOptional.map(CustomUserDetails::new).get();
    }

    @Transactional
    public CustomUserDetails loadUserByEmail(String email) throws EmailNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        userOptional.orElseThrow(() -> new EmailNotFoundException("Not found: " + email));
        return userOptional.map(CustomUserDetails::new).get();
    }

    @Transactional
    public User findById(Long id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userOptional.get();
    }

    @Transactional
    public void register(RegistrationDto registrationDto) throws IOException {
        User user = switch (registrationDto.getFormRole()) {
            case "ROLE_GENERAL_MANAGER" -> new GeneralManager();
            case "ROLE_PROJECT_MANAGER" -> new ProjectManager();
            case "ROLE_SUBMITTER" -> new Submitter();
            default -> new Developer();
        };

        user.setUsername(registrationDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setGender(registrationDto.getGender());
        user.setEmail(registrationDto.getEmail());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setRoles(
                new HashSet<>(
                        roleRepository.findByNameIn(Arrays.asList("ROLE_EMPLOYEE", registrationDto.getFormRole()))
                )
        );
        user.setAddress(createUserAddress(registrationDto));
        user.setEnabled(true);
        String fileName = setPhoto(registrationDto, user);

        User registeredUser = userRepository.save(user);

        saveProfilePicture(registeredUser, fileName, registrationDto.getPhoto());
    }

    @Transactional
    public void updateProfile(UserProfileDto userProfileDto, String username) throws IOException {
        User user = loadUserByUsername(username).getUser();

        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setGender(userProfileDto.getGender());
        user.setEmail(userProfileDto.getEmail());
        user.setPhoneNumber(userProfileDto.getPhoneNumber());
        user.setAddress(createUserAddress(userProfileDto));

        if (!(userProfileDto.getPhoto() == null || userProfileDto.getPhoto().isEmpty())) {
            String fileName = setPhoto(userProfileDto, user);
            saveProfilePicture(user, fileName, userProfileDto.getPhoto());
        }

        userRepository.save(user);
    }

    private String setPhoto(UserDto userDto, User user) {
        String fileName;
        if (userDto.getPhoto() == null || userDto.getPhoto().isEmpty()) {
            fileName = DEFAULT_PROFILE_PICTURE;

        } else {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(userDto.getPhoto().getOriginalFilename()));
        }

        user.setPhoto(fileName);
        return fileName;
    }

    public Address createUserAddress(UserDto userDto) {
        AddressDto addressDto = userDto.getAddress();
        return new Address(
                addressDto.getLine1(),
                addressDto.getLine2(),
                addressDto.getCity(),
                addressDto.getCounty()
        );
    }

    @Transactional
    public UserProfileDto findProfileDto(String username) {
        return new UserProfileDto(loadUserByUsername(username).getUser());
    }

    @Transactional
    public Set<User> findAllEmployees(Principal principal) {
        List<User> users;
        User user = loadUserByUsername(principal.getName()).getUser();

        if (hasRole(user,"ROLE_PROJECT_MANAGER")) {
            users = projectManagerService.findAllEmployees(principal.getName());
        } else {
            users = new ArrayList<>(userRepository.findAll());
        }

        return users.stream()
                .filter(o -> !o.getUsername().equals(principal.getName()))
                .filter(o -> !hasRole(o, "ROLE_GENERAL_MANAGER"))
                .sorted(Comparator.comparing(User::getUsername))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional
    public void delete(Long id) throws UserNotFoundException {
        User user = findById(id);

        if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_SUBMITTER"))) {
            Submitter submitter = (Submitter) user;
            submitter.getTickets().forEach(o -> o.setSubmitter(null));
        }

        userRepository.deleteById(id);
    }

    public void saveProfilePicture(User user, String fileName, MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/profile-pictures/" + user.getId();

        if (file == null || file.isEmpty()) {
            file = defaultProfilePicture();
        }

        FileUploadUtil.saveFile(uploadDir, fileName, file);
    }

    public MultipartFile defaultProfilePicture() throws IOException {
        return new MockMultipartFile("default-picture", new FileInputStream(
                new File("src/main/resources/profile-pictures/default/" + DEFAULT_PROFILE_PICTURE)));
    }

    public boolean hasRole(User user, String role) {
        return user.getRoles().stream().anyMatch(o -> o.getName().equals(role));
    }
}
