package com.example.cuoikyjavaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/index.html", "/login.html", "/register.html", "/css/**", "/js/**", "/images/**", "/static/**",
                    "/api/auth/register", "/api/auth/login"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/test-login.html")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }
} 