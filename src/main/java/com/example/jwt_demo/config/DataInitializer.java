package com.example.jwt_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.model.enums.Role;
import com.example.jwt_demo.repository.UserRepository;

@Configuration
public class DataInitializer {
    @Value("${admin.username}")
    private String username;
    @Value("${admin.email}")
    private String email;
    @Value("${admin.password}")
    private String password;

    @Bean
    CommandLineRunner initAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            boolean adminExists = userRepository.existsByRole(Role.ROLE_ADMIN);

            if (!adminExists) {
                var admin = User.builder()
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .role(Role.ROLE_ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("Usuario ADMIN creado");
            }
        };
    }
}