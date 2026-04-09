package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.dto.auth.ForgotPasswordRequest;
import com.scooterrental.backend.dto.auth.ResetPasswordRequest;
import com.scooterrental.backend.entity.User;
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
            data.put("token", "fake-jwt-token-demo"); // In real project, generate JWT here
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("role", user.getRole());

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

        boolean success = userService.register(user);

        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            return ResponseEntity.ok(Result.success(data));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Username already exists"));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Create password reset token")
    public ResponseEntity<Result<Map<String, Object>>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Email is required"));
        }

        Map<String, Object> resetData = userService.createPasswordReset(request.getEmail());
        if (resetData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "No account found with this email address"));
        }

        return ResponseEntity.ok(Result.success(resetData));
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

        if (request.getNewPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Password must be at least 6 characters"));
        }

        boolean success = userService.resetPassword(request.getToken(), request.getNewPassword());
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Invalid or expired reset token"));
        }

        return ResponseEntity.ok(Result.success());
    }
}
