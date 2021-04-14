package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.RoleRepository;
import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.dto.AddressDto;
import com.greally2014.ticketmanager.dto.RegistrationDto;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

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
    public void register(RegistrationDto registrationDto) {
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
        user.setEmail(registrationDto.getEmail());
        user.setRoles(
                new HashSet<>(
                        roleRepository.findByNameIn(Arrays.asList("ROLE_EMPLOYEE", registrationDto.getFormRole()))
                )
        );
        user.setEnabled(true);
    }

    @Transactional
    public void updateProfile(UserProfileDto userProfileDto, String principalUsername) {
        User user = loadUserByUsername(principalUsername).getUser();
        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setGender(userProfileDto.getGender());
        user.setEmail(userProfileDto.getEmail());
        user.setPhoneNumber(userProfileDto.getPhoneNumber());

        AddressDto addressDto = userProfileDto.getAddress();
        user.setAddress(new Address(
                addressDto.getLine1(),
                addressDto.getLine2(),
                addressDto.getCity(),
                addressDto.getCounty()
        ));
    }

    @Transactional
    public UserProfileDto findProfileDto(String username) {
        return new UserProfileDto(loadUserByUsername(username).getUser());
    }

    @Transactional
    public Set<User> findAllEmployeesByPrincipalRole(Principal principal) {
        List<String> principalRoles = loadUserByUsername(principal.getName()).getUser().getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        Set<User> users;

        if (principalRoles.contains("ROLE_PROJECT_MANAGER")) {
            users = projectManagerService.findAllEmployees(principal.getName());
        } else {
            users = new HashSet<>(userRepository.findAll());
        }

        return users.stream()
                .filter(o -> !o.equals(loadUserByUsername(principal.getName()).getUser()))
                .filter(o -> !(o.getRoles().stream().map(Role::getName).collect(Collectors.toSet()).contains("ROLE_GENERAL_MANAGER")))
                .collect(Collectors.toSet());
    }

    @Transactional
    public User findById(Long id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userOptional.get();
    }

    @Transactional
    public void delete(Long id) {
        try {
            User user = findById(id);

            if (user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()).contains("ROLE_SUBMITTER")) {
                Submitter submitter = (Submitter) user;

                for (Ticket ticket : submitter.getTickets()) {
                    ticket.setSubmitter(null);
                }
            }

            userRepository.deleteById(id);

        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }
}
