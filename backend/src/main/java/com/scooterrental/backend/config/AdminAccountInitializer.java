package com.scooterrental.backend.config;

import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private static final String ADMIN_USERNAME = "Admin_404";
    private static final String ADMIN_PASSWORD = "tyz114031";
    private static final String ADMIN_ROLE = "ADMIN";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminAccountInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User existingUser = userMapper.findByUsername(ADMIN_USERNAME);
        String encodedPassword = passwordEncoder.encode(ADMIN_PASSWORD);

        if (existingUser == null) {
            User adminUser = new User();
            adminUser.setUsername(ADMIN_USERNAME);
            adminUser.setEmail("admin_404@scootergo.local");
            adminUser.setCity("System");
            adminUser.setPasswordHash(encodedPassword);
            adminUser.setRole(ADMIN_ROLE);
            userMapper.insertWithRole(adminUser);
            return;
        }

        if (!ADMIN_ROLE.equalsIgnoreCase(existingUser.getRole())) {
            userMapper.updateUserRole(existingUser.getUserId(), ADMIN_ROLE);
        }

        if (existingUser.getPasswordHash() == null
                || !passwordEncoder.matches(ADMIN_PASSWORD, existingUser.getPasswordHash())) {
            userMapper.updatePasswordHash(existingUser.getUserId(), encodedPassword);
        }
    }
}
