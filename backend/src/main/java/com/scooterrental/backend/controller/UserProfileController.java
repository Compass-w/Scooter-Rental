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

@RestController
@RequestMapping("/api/users") // Standard path for user resources
@CrossOrigin(origins = "*")
@Tag(name = "User Profile Module", description = "Manage user profile and settings")
public class UserProfileController {

    @Autowired
    private UserService userService;

    /**
     * Get user profile info.
     * Future usage: Display user info on the "My Account" page in UniApp.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get User Profile", description = "Retrieve user details by ID")
    public ResponseEntity<Result<User>> getUserProfile(@PathVariable Integer id) {
        User user = userService.getUserById(id);

        if (user != null) {
            // Security Best Practice: Do not return the password hash to frontend!
            user.setPasswordHash(null);
            return ResponseEntity.ok(Result.success(user));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "User not found"));
        }
    }

    // Future: @PutMapping("/update") for updating avatar or phone number
}
