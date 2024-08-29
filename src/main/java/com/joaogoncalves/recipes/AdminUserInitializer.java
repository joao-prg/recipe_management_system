package com.joaogoncalves.recipes;

import com.joaogoncalves.recipes.entity.User;
import com.joaogoncalves.recipes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) {
        final String adminEmail = environment.getProperty("ADMIN_EMAIL");
        final String adminPassword = environment.getProperty("ADMIN_PASSWORD");

        if (adminEmail == null) {
            throw new IllegalArgumentException("Admin email not provided as environment variable.");
        }
        if (adminPassword == null) {
            throw new IllegalArgumentException("Admin password not provided as environment variable.");
        }

        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setAuthority("ROLE_ADMIN");

        userRepository.save(adminUser);
    }
}


