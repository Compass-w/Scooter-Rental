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
    private PasswordEncoder passwordEncoder;

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false;
        }
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);
        userMapper.insert(user);
        return true;
    }

    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    /**
     * Update user email and phone number.
     * 
     * @return true if update was successful.
     */
    public boolean updateUser(User user) {
        return userMapper.updateProfile(user) > 0;
    }

    /**
     * Gamification logic: check and award achievements [ID: 22].
     */
    public void checkAndAwardAchievements(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null)
            return;

        String current = user.getAchievements() != null ? user.getAchievements() : "";
        boolean updated = false;

        // Logic: Award "Eco-Warrior" medal if riding time >= 60 minutes
        if (user.getTotalRidingMinutes() != null && user.getTotalRidingMinutes() >= 60
                && !current.contains("Eco-Warrior")) {
            current = current.isEmpty() ? "Eco-Warrior" : current + ",Eco-Warrior";
            updated = true;
        }

        if (updated) {
            userMapper.updateAchievements(userId, current);
        }
    }
}
