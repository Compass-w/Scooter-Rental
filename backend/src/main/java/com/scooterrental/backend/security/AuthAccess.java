package com.scooterrental.backend.security;

import com.scooterrental.backend.service.AuthSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AuthAccess {

    private AuthAccess() {
    }

    public static boolean isStaff(AuthSessionService.AuthSession session) {
        if (session == null || session.role() == null) {
            return false;
        }

        String role = session.role().trim().toUpperCase();
        return "ADMIN".equals(role) || "MANAGER".equals(role);
    }

    public static Integer requireAuthenticatedUserId(AuthSessionService.AuthSession session) {
        if (session == null || session.userId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required");
        }

        return session.userId();
    }

    public static void requireSelfOrStaff(AuthSessionService.AuthSession session, Integer requestedUserId) {
        Integer currentUserId = requireAuthenticatedUserId(session);
        if (requestedUserId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        if (!isStaff(session) && !currentUserId.equals(requestedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own data");
        }
    }
}
