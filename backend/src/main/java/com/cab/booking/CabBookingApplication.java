package com.cab.booking;

import com.cab.booking.entity.User;
import com.cab.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CabBookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CabBookingApplication.class, args);
    }

    // Creates a default admin once: admin@cab.com / admin123
    @Bean
    CommandLineRunner seedAdmin(UserRepository repo) {
        return args -> {
            if (!repo.existsByEmail("admin@cab.com")) {
                User u = new User();
                u.setName("Admin");
                u.setEmail("admin@cab.com");
                u.setPassword(new BCryptPasswordEncoder().encode("admin123"));
                u.setRole("ADMIN");
                repo.save(u);
            }
        };
    }
}
