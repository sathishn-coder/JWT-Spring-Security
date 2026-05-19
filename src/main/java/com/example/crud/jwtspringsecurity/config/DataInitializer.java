package com.example.crud.jwtspringsecurity.config;


import com.example.crud.jwtspringsecurity.entity.Role;
import com.example.crud.jwtspringsecurity.entity.User;
import com.example.crud.jwtspringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer — seeds the database with default users on first startup.
 *
 * Implements CommandLineRunner so it runs after the application context
 * is fully loaded (Spring Security, JPA, etc. are all ready).
 *
 * Default credentials created:
 *  Admin: username=admin  / password=Admin@1234
 *  User:  username=user   / password=User@1234
 *
 * ⚠️ Change these credentials in production!
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminIfNotExists();
        createDefaultUserIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (userRepository.existsByUsername("admin")) {
            log.info("Admin user already exists — skipping seed");
            return;
        }

        User admin = User.builder()
                .firstName("System")
                .lastName("Admin")
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("Admin@1234"))
                .role(Role.ROLE_ADMIN)
                .isActive(true)
                .build();

        userRepository.save(admin);
        log.info("✅ Admin user created → username: admin | password: Admin@1234");
    }

    private void createDefaultUserIfNotExists() {
        if (userRepository.existsByUsername("user")) {
            return;
        }

        User user = User.builder()
                .firstName("Default")
                .lastName("User")
                .username("user")
                .email("user@example.com")
                .password(passwordEncoder.encode("User@1234"))
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        userRepository.save(user);
        log.info("✅ Default user created → username: user | password: User@1234");
    }
}
