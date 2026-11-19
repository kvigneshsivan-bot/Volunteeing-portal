package com.maatram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.maatram.entity.Role;
import com.maatram.entity.User;
import com.maatram.repository.UserRepository;

@SpringBootApplication
public class MaatramApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaatramApplication.class, args);
    }

    @Bean
    CommandLineRunner bootstrapSuperAdmin(UserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "k.vigneshsivan@gmail.com";
            String password = System.getenv().getOrDefault("MAATRAM_SUPER_ADMIN_PASSWORD", "admin@12345");
            User superAdmin = users.findByEmailIgnoreCase(email).orElseGet(User::new);
            if (superAdmin.getId() == null) {
                superAdmin.setFullName("Vignesh Sivan");
                superAdmin.setVolunteerId("SUPER001");
                superAdmin.setEmail(email);
                superAdmin.setMobileNumber("9999999999");
                superAdmin.setAcademicYear("Admin");
            }
            superAdmin.setRole(Role.SUPER_ADMIN);
            superAdmin.setAccountStatus(com.maatram.entity.AccountStatus.ACTIVE);
            superAdmin.setPassword(passwordEncoder.encode(password));
            superAdmin.setActive(true);
            users.save(superAdmin);
        };
    }
}
