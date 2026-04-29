package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final long RESET_TOKEN_EXPIRY_HOURS = 24;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.default-manager.enabled:false}")
    private boolean defaultManagerBootstrapEnabled;

    private final Map<String, PasswordResetSession> passwordResetTokens = new ConcurrentHashMap<>();
    private volatile boolean bootstrapChecked = false;

    public User login(String username, String password) {
        ensureBootstrapState();

        User user = userMapper.findByUsername(username);
        if (user == null || password == null) {
            return null;
        }

        String storedPassword = user.getPasswordHash();
        if (storedPassword == null || storedPassword.isBlank()) {
            return null;
        }

        try {
            if (looksLikeBcryptHash(storedPassword) && passwordEncoder.matches(password, storedPassword)) {
                return user;
            }
        } catch (IllegalArgumentException ignored) {
            // Fall back to legacy password compatibility below.
        }

        // Compatibility path for legacy rows that still store a plain-text or otherwise
        // non-BCrypt password. When the raw password matches, transparently upgrade it.
        if (password.equals(storedPassword)) {
            userMapper.updatePasswordHash(user.getUserId(), passwordEncoder.encode(password));
            user.setPasswordHash(null);
            return user;
        }

        return null;
    }

    public boolean register(User user) {
        ensureBootstrapState();

        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false;
        }
        if (user.getEmail() != null && !user.getEmail().isBlank() && userMapper.findByEmail(user.getEmail()) != null) {
            return false;
        }
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);
        userMapper.insert(user);
        return true;
    }

    public User getUserById(Integer userId) {
        ensureBootstrapState();
        return userMapper.selectById(userId);
    }

    public boolean emailBelongsToAnotherUser(Integer userId, String email) {
        ensureBootstrapState();

        if (email == null || email.isBlank()) {
            return false;
        }

        return userMapper.countByEmailExcludingUserId(email.trim(), userId == null ? -1 : userId) > 0;
    }

    public PasswordResetRequestData createPasswordReset(String email, Integer preferredUserId) {
        ensureBootstrapState();

        if ((email == null || email.isBlank()) && preferredUserId == null) {
            return null;
        }

        User user = null;
        if (preferredUserId != null) {
            user = userMapper.selectById(preferredUserId);
        }

        if (user == null && email != null && !email.isBlank()) {
            user = userMapper.findByEmail(email.trim());
        }

        if (user == null || user.getUserId() == null) {
            return null;
        }

        invalidateResetTokensForUser(user.getUserId());

        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRY_HOURS);
        passwordResetTokens.put(token, new PasswordResetSession(user.getUserId(), user.getEmail(), expiresAt));

        return new PasswordResetRequestData(
                user.getUserId(),
                user.getEmail(),
                user.getPhone(),
                token,
                expiresAt);
    }

    public Map<String, Object> verifyPasswordResetToken(String token) {
        PasswordResetSession session = getValidResetSession(token);
        if (session == null) {
            return null;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", session.email());
        payload.put("expiresAt", session.expiresAt());
        return payload;
    }

    public boolean resetPassword(String token, String newPassword) {
        ensureBootstrapState();

        PasswordResetSession session = getValidResetSession(token);
        if (session == null || newPassword == null || newPassword.isBlank()) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        int updatedRows = userMapper.updatePasswordHash(session.userId(), encodedPassword);
        if (updatedRows > 0) {
            invalidateResetTokensForUser(session.userId());
            return true;
        }

        return false;
    }

    /**
     * Update user email and phone number.
     * 
     * @return true if update was successful.
     */
    public boolean updateUser(User user) {
        ensureBootstrapState();

        if (user == null || user.getUserId() == null) {
            return false;
        }

        if (emailBelongsToAnotherUser(user.getUserId(), user.getEmail())) {
            return false;
        }

        return userMapper.updateUser(user) > 0;
    }

    /**
     * Gamification logic: check and award achievements [ID: 22].
     */
    public void checkAndAwardAchievements(Integer userId) {
        ensureBootstrapState();

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

    private void ensureBootstrapState() {
        if (bootstrapChecked) {
            return;
        }

        synchronized (this) {
            if (bootstrapChecked) {
                return;
            }

            try {
                if (defaultManagerBootstrapEnabled) {
                    ensureDefaultManagerAccount();
                }
                bootstrapChecked = true;
            } catch (Exception ex) {
                log.error("Failed to initialize optional bootstrap data", ex);
            }
        }
    }

    private void ensureDefaultManagerAccount() {
        int updatedRows = userMapper.updateDefaultManagerAccount();
        if (updatedRows == 0) {
            userMapper.insertDefaultManagerAccountIfMissing();
        }
    }

    private PasswordResetSession getValidResetSession(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        PasswordResetSession session = passwordResetTokens.get(token.trim());
        if (session == null) {
            return null;
        }

        if (session.expiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokens.remove(token.trim());
            return null;
        }

        return session;
    }

    private void invalidateResetTokensForUser(Integer userId) {
        passwordResetTokens.entrySet().removeIf(entry -> Objects.equals(entry.getValue().userId(), userId));
    }

    private boolean looksLikeBcryptHash(String passwordHash) {
        return passwordHash.startsWith("$2a$")
                || passwordHash.startsWith("$2b$")
                || passwordHash.startsWith("$2y$");
    }

    public record PasswordResetRequestData(
            Integer userId,
            String email,
            String phone,
            String token,
            LocalDateTime expiresAt) {
    }

    private record PasswordResetSession(Integer userId, String email, LocalDateTime expiresAt) {
    }
}
