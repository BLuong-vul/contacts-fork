package com.vision.middleware;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Role;
import com.vision.middleware.repo.RoleRepository;
import com.vision.middleware.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

/**
 * Spring Boot Application entry point.
 * <p>
 * This application sets up a basic admin user and role structure on startup.
 */
@SpringBootApplication
public class Application {

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Setup function that runs each time the application is started.
     * It ensures that the ADMIN role and user exist in the system.
     *
     * @param roleRepository    Repository for Role operations.
     * @param userRepository    Repository for ApplicationUser operations.
     * @param passwordEncoder   Password encoder for user passwords.
     * @return CommandLineRunner instance.
     */
    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if ADMIN role already exists
            if (roleRepository.findByAuthority("ADMIN").isPresent()) return;

            // Save ADMIN and USER roles
            Role adminRole = roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("USER"));

            // Set up roles for the admin user
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            // Create admin user with encoded password
            // todo: factor out admin password later, probably to env var.
            ApplicationUser admin = ApplicationUser.builder()
                    .id(1)
                    .username("admin")
                    .fullName("Admin")
                    .email("N/A")
                    .phoneNumber("N/A")
                    .address("N/A")
                    .city("N/A")
                    .state("N/A")
                    .zipCode("N/A")
                    .country("N/A")
                    .password(passwordEncoder.encode("password"))
                    .authorities(roles)
                    .build();

            // Save admin user to the repository
            userRepository.save(admin);
        };
    }
}
