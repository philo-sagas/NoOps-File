package com.sagas.noops.file.config;

import com.sagas.noops.file.constants.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Autowired
    private ApplicationConstants applicationConstants;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails adminUserDetails = User.withUsername("admin")
                .password(encoder.encode(applicationConstants.getAdminPassword()))
                .authorities("ROLE_ADMIN").build();
        UserDetails guestUserDetails = User.withUsername("guest")
                .password(encoder.encode(applicationConstants.getGuestPassword()))
                .authorities("ROLE_GUEST").build();
        return new MapReactiveUserDetailsService(adminUserDetails, guestUserDetails);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/webjars/**", "/assets/**", "/favicon.ico", "/login").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(formLoginSpec -> formLoginSpec.loginPage("/login"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
