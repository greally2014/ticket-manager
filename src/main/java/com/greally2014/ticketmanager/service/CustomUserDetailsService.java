package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.RoleRepository;
import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.formModel.CustomUserDetails;
import com.greally2014.ticketmanager.user.RegistrationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(username);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
        return user.map(CustomUserDetails::new).get();
    }

    @Transactional
    public UserDetails loadUserByEmail(String email) throws EmailNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(() -> new EmailNotFoundException("Not found: " + email));
        return user.map(CustomUserDetails::new).get();
    }

    @Transactional
    public void save(RegistrationUser registrationUser) throws InputMismatchException {
        User user = switch (registrationUser.getFormRole()) {
            case "ROLE_GENERAL_MANAGER" -> new GeneralManager();
            case "ROLE_PROJECT_MANAGER" -> new ProjectManager();
            case "ROLE_SUBMITTER" -> new Submitter();
            case "ROLE_DEVELOPER" -> new Developer();
            default -> throw new InputMismatchException("Formrole not recognised");
        };

        user.setUserName(registrationUser.getUserName());
        user.setPassword(bCryptPasswordEncoder.encode(registrationUser.getPassword()));
        user.setFirstName(registrationUser.getFirstName());
        user.setLastName(registrationUser.getLastName());
        user.setEmail(registrationUser.getEmail());
        user.setRoles(getRegisteredUserRoles(registrationUser.getFormRole()));
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Transactional
    public Set<Role> getRegisteredUserRoles(String formRole) {
        List<String> roles = new ArrayList<>(Arrays.asList("ROLE_EMPLOYEE", formRole));
        return new HashSet<>(roleRepository.findByNameIn(roles));
    }
}
