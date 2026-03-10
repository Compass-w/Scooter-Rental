package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injected from SecurityConfig

    /**
     * Authenticate user.
     * Compares raw password with the BCrypt hash in database.
     */
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);

        // Check if user exists AND password matches the hash
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        return null; // Authentication failed
    }

    /**
     * Register new user.
     * Encrypts the password before saving.
     */
    public boolean register(User user) {
        // 1. Check if username already exists
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false;
        }

        // 2. Encrypt password using BCrypt
        String rawPassword = user.getPasswordHash(); // Raw input from frontend
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPasswordHash(encodedPassword);

        // 3. Insert into database
        userMapper.insert(user);
        return true;
    }

    /**
     * Get user profile by ID.
     * Used for the /api/users/{id} endpoint.
     */
    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    // Add this method to handle achievement logic
    public void checkAndAwardAchievements(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null)
            return;

        String currentAchievements = user.getAchievements() != null ? user.getAchievements() : "";
        boolean updated = false;

        // Rule 1: Award "Eco-Warrior" if total minutes > 60
        if (user.getTotalRidingMinutes() >= 60 && !currentAchievements.contains("Eco-Warrior")) {
            currentAchievements = currentAchievements.isEmpty() ? "Eco-Warrior" : currentAchievements + ",Eco-Warrior";
            updated = true;
        }

        // Rule 2: Award "Newcomer" for the first ride
        if (user.getTotalRidingMinutes() > 0 && !currentAchievements.contains("Newcomer")) {
            currentAchievements = currentAchievements.isEmpty() ? "Newcomer" : currentAchievements + ",Newcomer";
            updated = true;
        }

        if (updated) {
            // You would need a simple update method in UserMapper
            userMapper.updateAchievements(userId, currentAchievements);
        }
    }
}
