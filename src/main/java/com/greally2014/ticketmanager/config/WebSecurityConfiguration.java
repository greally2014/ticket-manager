package com.greally2014.ticketmanager.config;

import com.greally2014.ticketmanager.security.CustomAuthenticationFailureHandler;
import com.greally2014.ticketmanager.security.CustomAuthenticationSuccessHandler;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public WebSecurityConfiguration(CustomUserDetailsService customUserDetailsService,
                                    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
                                    CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/projects")
                // only users with these roles can access /projects
                    .hasAnyRole("GENERAL_MANAGER", "PROJECT_MANAGER")
                .antMatchers("/")
                // user must have role EMPLOYEE to access the application
                    .hasRole("EMPLOYEE")
            .and()
            .formLogin()
                .loginPage("/login") // login page returned by controller with mapping /login
                .loginProcessingUrl("/processLogin") // method with mapping /processLogin responsible for processing login attempt
                .usernameParameter("username")
                .passwordParameter("password")
                // our newly defined authentication handlers are passed into spring security to use
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
            .and()
            .logout()
                .invalidateHttpSession(true)
                .logoutUrl("/processLogout") // url required to logout
                .permitAll()
            .and()
            .exceptionHandling()
                // page displayed when a user attempts to call functionality they should not be allowed to use
                .accessDeniedPage("/access-denied");
    }

    //authenticationProvider bean definition
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customUserDetailsService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
