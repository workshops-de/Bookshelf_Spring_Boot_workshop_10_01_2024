package de.workshops.bookshelf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class SpringSecurityTestConfiguration {

    @Bean
    public UserDetailsService testUserDetailsService() {
        var user = User.builder().username("user").password("password").roles("USER").build();
        var admin = User.builder().username("admin").password("admin").roles("USER", "ADMIN").build();
        return new InMemoryUserDetailsManager(user, admin);    }

    @Bean
    public PasswordEncoder testPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    //see: https://github.com/spring-projects/spring-boot/issues/36949#issuecomment-1683520465
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(testUserDetailsService());
        auth.setPasswordEncoder(testPasswordEncoder());
        return auth;
    }
}