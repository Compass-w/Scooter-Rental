package com.scooterrental.backend.controller;

import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 允许跨域
@Tag(name = "User Management", description = "APIs for user login and registration")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user with username and password")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = userService.login(username, password);
        
        Map<String, Object> response = new HashMap<>();
        
        if (user != null) {
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("userId", user.getUserId());
            response.put("role", user.getRole());
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
        }
        
        return response;
    }
}
