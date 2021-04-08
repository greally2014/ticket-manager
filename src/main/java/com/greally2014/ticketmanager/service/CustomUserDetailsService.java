package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.RoleRepository;
import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.formModel.ProfileFormUser;
import com.greally2014.ticketmanager.formModel.RegistrationFormUser;
import com.greally2014.ticketmanager.userDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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
    public ProfileFormUser getProfileFormUser(String username) {
        return new ProfileFormUser(userRepository.findByUsername(username).get());
    }

    @Transactional
    public boolean isUsernameTaken(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Transactional
    public void save(RegistrationFormUser registrationFormUser) throws RoleNotFoundException {
        User user = switch (registrationFormUser.getFormRole()) {
            case "ROLE_GENERAL_MANAGER" -> new GeneralManager();
            case "ROLE_PROJECT_MANAGER" -> new ProjectManager();
            case "ROLE_SUBMITTER" -> new Submitter();
            case "ROLE_DEVELOPER" -> new Developer();
            default -> throw new RoleNotFoundException("registrationFormUser.formrole INCORRECT");
        };

        user.setUsername(registrationFormUser.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(registrationFormUser.getPassword()));
        user.setFirstName(registrationFormUser.getFirstName());
        user.setLastName(registrationFormUser.getLastName());
        user.setEmail(registrationFormUser.getEmail());
        user.setRoles(new HashSet<>(roleRepository.findByNameIn(
                Arrays.asList("ROLE_EMPLOYEE", registrationFormUser.getFormRole())
        )));
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Transactional
    public void updateProfile(ProfileFormUser profileFormUser, String principalUsername) {
        userRepository.updateProfileDetails(profileFormUser.getUsername(), profileFormUser.getFirstName(),
                profileFormUser.getLastName(), profileFormUser.getEmail(), principalUsername);
    }
}
