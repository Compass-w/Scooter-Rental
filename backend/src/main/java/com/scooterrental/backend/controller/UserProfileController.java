package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.service.UserService;
import com.scooterrental.backend.service.BookingService; // Assuming this exists from previous turns
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Profile Module", description = "Manage user profile and settings")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/{id}")
    @Operation(summary = "Get User Profile", description = "Retrieve user details and refresh achievements")
    public ResponseEntity<Result<User>> getUserProfile(@PathVariable Integer id) {
        // Refresh achievements before returning
        userService.checkAndAwardAchievements(id);

        User user = userService.getUserById(id);
        if (user != null) {
            user.setPasswordHash(null);
            return ResponseEntity.ok(Result.success(user));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "User not found"));
        }
    }

    // --- Added to match frontend: updateUserInfo ---
    @PutMapping("/update")
    @Operation(summary = "Update Profile", description = "Update user email and phone number")
    public Result<String> updateUserInfo(@RequestBody User user) {
        if (userService.updateUser(user)) {
            return Result.success("Profile updated successfully");
        }
        return Result.error(400, "Failed to update profile");
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get Usage Stats", description = "Weekly riding minutes for chart display [ID: 22]")
    public Result<List<Map<String, Object>>> getStats(@PathVariable Integer id) {
        return Result.success(bookingService.getStats(id));
    }
}
