package com.thecodealchemist.main.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF (re-enable in production if needed)
                .cors(Customizer.withDefaults()) // Enable CORS
                .authorizeHttpRequests(authReq -> {
                    authReq
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow OPTIONS requests for preflight
                            .requestMatchers("/api/user/register", "/api/user/login").permitAll() // Allow public endpoints
                            .anyRequest().authenticated(); // Secure other requests
                })
                .httpBasic(Customizer.withDefaults()) // Use HTTP Basic Auth (or replace with another method)
                .build();
    }
    

     @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] digest = md.digest(rawPassword.toString().getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (byte b : digest) {
                        sb.append(String.format("%02x", b));
                    }
                    return sb.toString();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("Error hashing password with MD5", e);
                }
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encode(rawPassword).equals(encodedPassword);
            }
        };
    }
}
