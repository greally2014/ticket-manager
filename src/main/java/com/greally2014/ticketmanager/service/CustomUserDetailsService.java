package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.RoleRepository;
import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.dto.AddressDto;
import com.greally2014.ticketmanager.dto.RegistrationDto;
import com.greally2014.ticketmanager.dto.UserProfileDto;
import com.greally2014.ticketmanager.entity.*;
import com.greally2014.ticketmanager.exception.EmailNotFoundException;
import com.greally2014.ticketmanager.exception.NumberNotFoundException;
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
    public CustomUserDetails loadUserByPhoneNumber(String number) throws NumberNotFoundException {
        Optional<User> userOptional = userRepository.findByPhoneNumber(number);
        userOptional.orElseThrow(() -> new NumberNotFoundException("Not found: " + number));
        return userOptional.map(CustomUserDetails::new).get();
    }

    @Transactional
    public boolean phoneNumberExists(String number) {
        return userRepository.existsByPhoneNumber(number);
    }


    @Transactional
    public void register(RegistrationDto registrationDto) throws RoleNotFoundException {
        User user = switch (registrationDto.getFormRole()) {
            case "ROLE_GENERAL_MANAGER" -> new GeneralManager();
            case "ROLE_PROJECT_MANAGER" -> new ProjectManager();
            case "ROLE_SUBMITTER" -> new Submitter();
            case "ROLE_DEVELOPER" -> new Developer();
            default -> throw new RoleNotFoundException("registrationDto.formrole INCORRECT");
        };

        user.setUsername(registrationDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setRoles(new HashSet<>(roleRepository.findByNameIn(
                Arrays.asList("ROLE_EMPLOYEE", registrationDto.getFormRole())
        )));
        user.setEnabled(true);
    }

    @Transactional
    public void updateProfile(UserProfileDto userProfileDto, String principalUsername) {
        User user = loadUserByUsername(principalUsername).getUser();
        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setGender(userProfileDto.getGender());
        user.setEmail(userProfileDto.getEmail());

        AddressDto addressDto = userProfileDto.getAddress();

        user.setAddress(new Address(
                addressDto.getLine1(),
                addressDto.getLine2(),
                addressDto.getCity(),
                addressDto.getCounty()
        ));

        String phoneNumber = userProfileDto.getPhoneNumber();
        user.setPhoneNumber(userProfileDto.getPhoneNumber());
    }

    @Transactional
    public UserProfileDto getProfileDto(String username) throws UsernameNotFoundException {
        return new UserProfileDto(loadUserByUsername(username).getUser());
    }
}
