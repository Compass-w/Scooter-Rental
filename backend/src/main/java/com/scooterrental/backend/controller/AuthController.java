package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
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
}
