package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthSessionService {

    private final Map<String, AuthSession> sessions = new ConcurrentHashMap<>();
    private final Map<Integer, String> latestSessionByUserId = new ConcurrentHashMap<>();

    public synchronized String createSession(User user) {
        String token = "session-" + UUID.randomUUID();
        Integer userId = user == null ? null : user.getUserId();
        sessions.put(token, new AuthSession(
                token,
                userId,
                user == null ? null : user.getUsername(),
                user == null ? null : user.getEmail(),
                normalizeRole(user == null ? null : user.getRole()),
                LocalDateTime.now()));

        if (userId != null) {
            String previousToken = latestSessionByUserId.put(userId, token);
            if (previousToken != null && !previousToken.equals(token)) {
                sessions.remove(previousToken);
            }
        }

        return token;
    }

    public AuthSession resolveSession(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return sessions.get(token.trim());
    }

    public synchronized void revokeSession(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        String normalizedToken = token.trim();
        AuthSession removed = sessions.remove(normalizedToken);
        if (removed != null && removed.userId() != null) {
            latestSessionByUserId.remove(removed.userId(), normalizedToken);
        }
    }

    public String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "CUSTOMER";
        }
        return role.trim().toUpperCase(Locale.ROOT);
    }

    public record AuthSession(
            String token,
            Integer userId,
            String username,
            String email,
            String role,
            LocalDateTime createdAt) {
    }
}
