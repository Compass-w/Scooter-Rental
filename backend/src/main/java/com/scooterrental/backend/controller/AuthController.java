package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.dto.auth.ForgotPasswordRequest;
import com.scooterrental.backend.dto.auth.ResetPasswordRequest;
import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.service.AuthSessionService;
import com.scooterrental.backend.service.NotificationService;
import com.scooterrental.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Standard path for authentication
@CrossOrigin(origins = "*")
@Tag(name = "Authentication Module", description = "Handle Login and Registration")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthSessionService authSessionService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Returns token if success, HTTP 401 if failed")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        // Basic validation
        if (username == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Username and password are required"));
        }

        User user = userService.login(username, password);

        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", authSessionService.createSession(user));
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("role", authSessionService.normalizeRole(user.getRole()));

            // Login Success -> 200 OK
            return ResponseEntity.ok(Result.success(data));
        } else {
            // Login Failed -> 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User Register")
    public ResponseEntity<Result<Map<String, Object>>> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getPasswordHash() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Missing required fields"));
        }

        String passwordValidationMessage = validatePasswordComplexity(user.getPasswordHash());
        if (passwordValidationMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, passwordValidationMessage));
        }

        boolean success = userService.register(user);

        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            return ResponseEntity.ok(Result.success(data));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Username or email already exists"));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Create password reset token")
    public ResponseEntity<Result<Map<String, Object>>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request == null || request.getEmail() == null ? null : request.getEmail().trim();
        Integer userId = request == null ? null : request.getUserId();

        if ((email == null || email.isBlank()) && userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Email or user ID is required"));
        }

        UserService.PasswordResetRequestData resetRequest = userService.createPasswordReset(email, userId);
        if (resetRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "No matching account found for this reset request"));
        }

        String resetLink = notificationService.buildPasswordResetLink(resetRequest.token());
        boolean emailSent = notificationService.sendPasswordResetEmail(
                resetRequest.email(),
                resetLink,
                resetRequest.expiresAt());
        if (!emailSent) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Result.error(503, "Password reset email service is unavailable"));
        }

        boolean smsSent = notificationService.sendPasswordResetSms(
                resetRequest.phone(),
                resetRequest.email(),
                resetLink,
                resetRequest.expiresAt());

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", resetRequest.email());
        payload.put("expiresAt", resetRequest.expiresAt());
        payload.put("emailSent", true);
        payload.put("smsSent", smsSent);
        return ResponseEntity.ok(Result.success(payload));
    }

    @GetMapping("/verify-reset-token")
    @Operation(summary = "Verify password reset token")
    public ResponseEntity<Result<Map<String, Object>>> verifyResetToken(@RequestParam String token) {
        Map<String, Object> tokenData = userService.verifyPasswordResetToken(token);
        if (tokenData == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Invalid or expired reset token"));
        }

        return ResponseEntity.ok(Result.success(tokenData));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<Result<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request == null || request.getToken() == null || request.getToken().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Reset token is required"));
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "New password is required"));
        }

        String passwordValidationMessage = validatePasswordComplexity(request.getNewPassword());
        if (passwordValidationMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, passwordValidationMessage));
        }

        boolean success = userService.resetPassword(request.getToken(), request.getNewPassword());
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Invalid or expired reset token"));
        }

        return ResponseEntity.ok(Result.success());
    }

    private String validatePasswordComplexity(String password) {
        if (password == null || password.isBlank()) {
            return "Password is required";
        }

        boolean hasMinLength = password.length() >= 8;
        boolean hasLetter = password.matches(".*[A-Za-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialCharacter = password.matches(".*[^A-Za-z0-9].*");

        if (hasMinLength && hasLetter && hasNumber && hasSpecialCharacter) {
            return null;
        }

        return "Password must be at least 8 characters and include letters, numbers, and special characters";
    }
}
