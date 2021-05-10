package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.ResetPasswordCodeRepository;
import com.greally2014.ticketmanager.dao.RoleRepository;
import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.dao.VerificationCodeRepository;
import com.greally2014.ticketmanager.dto.user.AddressDto;
import com.greally2014.ticketmanager.dto.user.RegistrationDto;
import com.greally2014.ticketmanager.dto.user.UserDto;
import com.greally2014.ticketmanager.dto.user.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.utility.Util;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import net.bytebuddy.utility.RandomString;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String DEFAULT_PROFILE_PICTURE = "default-picture.jpg";

    public static final int MAX_FAILED_ATTEMPTS = 3;

    public static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ProjectManagerService projectManagerService;

    private final JavaMailSender mailSender;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final VerificationCodeRepository verificationCodeRepository;

    private final ResetPasswordCodeRepository resetPasswordCodeRepository;

    public CustomUserDetailsService(
            @Lazy BCryptPasswordEncoder bCryptPasswordEncoder,
            ProjectManagerService projectManagerService,
            RoleRepository roleRepository,
            UserRepository userRepository,
            VerificationCodeRepository verificationCodeRepository,
            ResetPasswordCodeRepository resetPasswordCodeRepository,
            JavaMailSender mailSender) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.projectManagerService = projectManagerService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
        this.mailSender = mailSender;
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
    public void register(RegistrationDto registrationDto, String siteUrl) throws IOException, MessagingException {
        User user = switch (registrationDto.getFormRole()) {
            case "ROLE_GENERAL_MANAGER" -> new GeneralManager();
            case "ROLE_PROJECT_MANAGER" -> new ProjectManager();
            case "ROLE_SUBMITTER" -> new Submitter();
            default -> new Developer();
        };

        user.setUsername(registrationDto.getUsername());

        String encodedPassword = bCryptPasswordEncoder.encode(registrationDto.getPassword());
        user.setPassword(encodedPassword);

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
        user.setAccountNonLocked(true);

        String fileName = setPhoto(registrationDto, user);
        String randomCode = RandomString.make(64);
        VerificationCode verificationCode = new VerificationCode(randomCode);
        user.setVerificationCode(verificationCode);

        User registeredUser = userRepository.save(user);

        sendVerificationEmail(registeredUser, siteUrl);

        saveProfilePicture(registeredUser, fileName, registrationDto.getPhoto());
    }

    private void sendVerificationEmail(User user, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "greally2014@gmail.com";
        String senderName = "Daniel Greally";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "YOU HAVE 24 HOURS TO VERIFY YOUR ACCOUNT<br><br>"
                + "Thank you,<br>"
                + "Ticket Manager Company";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());
        String verifyUrl = siteUrl + "/registration/verify?code=" + user.getVerificationCode().getCode();
        content = content.replace("[[URL]]", verifyUrl);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @Transactional
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        Calendar calendar = Calendar.getInstance();

        if (user == null || user.isEnabled()) {
            return false;

        } else if ((user.getVerificationCode().getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0) {
            try {
                delete(user.getId());
            } catch (UserNotFoundException e) {
                //nothing
            }
            return false;

        } else {

            verificationCodeRepository.delete(user.getVerificationCode());
            user.setVerificationCode(null);

            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }
    }

    @Transactional
    public void updateResetPasswordCode(String code, String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            ResetPasswordCode resetPasswordCode = new ResetPasswordCode(code);
            user.get().setResetPasswordCode(resetPasswordCode);
            userRepository.save(user.get());
        } else {
            throw new UserNotFoundException("Could not find user with email: " + email);
        }
    }

    @Transactional
    public User getUserByResetPasswordCode(String code) {
        return userRepository.findByResetPasswordCode(code);
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        resetPasswordCodeRepository.delete(user.getResetPasswordCode());
        user.setResetPasswordCode(null);
        userRepository.save(user);
    }

    @Transactional
    public void increaseFailedAttempts(User user) {
        int newFailedAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailedAttempts, user.getEmail());
    }

    @Transactional
    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(0, email);
    }

    @Transactional
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());

        userRepository.save(user);
    }

    @Transactional
    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            userRepository.save(user);
        }

        return false;
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

    @Transactional
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
    public User delete(Long id) throws UserNotFoundException {
        User user = findById(id);

        if (user.getRoles().stream().anyMatch(o -> o.getName().equals("ROLE_SUBMITTER"))) {
            Submitter submitter = (Submitter) user;
            submitter.getTickets().forEach(o -> o.setSubmitter(null));
        }

        userRepository.deleteById(id);
        return user;
    }

    @Transactional
    public void deleteByUsername(RegistrationDto registrationDto) {
        userRepository.deleteByUsername(registrationDto.getUsername());
    }

    @Transactional
    public void saveProfilePicture(User user, String fileName, MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/profile-pictures/" + user.getId();

        if (file == null || file.isEmpty()) {
            file = defaultProfilePicture();
        }

        Util.saveFile(uploadDir, fileName, file);
    }

    @Transactional
    public MultipartFile defaultProfilePicture() throws IOException {
        return new MockMultipartFile("default-picture", new FileInputStream(
                new File("src/main/resources/profile-pictures/default/" + DEFAULT_PROFILE_PICTURE)));
    }

    @Transactional
    public boolean hasRole(User user, String role) {
        return user.getRoles().stream().anyMatch(o -> o.getName().equals(role));
    }

}
