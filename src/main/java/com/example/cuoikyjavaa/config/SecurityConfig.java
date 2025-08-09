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
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/favicon.ico",
                                "/admin/**",
                                "/giangvien/**",
                                "/nhanvien/**",
                                "/sinhvien/**",
                                "/kythuatvien/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

}
