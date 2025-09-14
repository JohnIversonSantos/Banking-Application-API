package com.ciicc.Banking_Application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)   // disable CSRF for Postman testing
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()            // ✅ allow all requests (no 401)
                )
                .formLogin(AbstractHttpConfigurer::disable)  // disable form login
                .httpBasic(AbstractHttpConfigurer::disable); // disable basic auth

        return http.build();
    }
}
