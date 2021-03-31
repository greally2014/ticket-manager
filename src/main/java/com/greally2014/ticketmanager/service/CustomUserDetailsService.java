package com.greally2014.ticketmanager.service;

import com.greally2014.ticketmanager.dao.RoleRepository;
import com.greally2014.ticketmanager.dao.UserRepository;
import com.greally2014.ticketmanager.entity.Role;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.model.CustomUserDetails;
import com.greally2014.ticketmanager.user.RegistrationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(username);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
        return user.map(CustomUserDetails::new).get();
    }

    public void save(RegistrationUser registrationUser) {
        User user = new User();

        user.setUserName(registrationUser.getUserName());
        user.setPassword(bCryptPasswordEncoder.encode(registrationUser.getPassword()));
        user.setFirstName(registrationUser.getFirstName());
        user.setLastName(registrationUser.getLastName());
        user.setEmail(registrationUser.getEmail());
        user.setRoles(getRegisteredUserRoles(registrationUser.getFormRole()));
        user.setEnabled(true);

        userRepository.save(user);
    }

    public Set<Role> getRegisteredUserRoles(String formRole) {
        List<String> roles = new ArrayList<>(Arrays.asList("ROLE_EMPLOYEE", formRole));
        return new HashSet<>(roleRepository.findByNameIn(roles));
    }


}
