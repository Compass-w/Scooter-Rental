package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.service.UserService;
import com.scooterrental.backend.service.BookingService;
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

    /**
     * Get user profile details and refresh achievements.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get User Profile", description = "Retrieve user details and auto-refresh medals")
    public ResponseEntity<Result<User>> getUserProfile(@PathVariable Integer id) {
        userService.checkAndAwardAchievements(id);
        User user = userService.getUserById(id);

        if (user != null) {
            user.setPasswordHash(null); // Security: remove password hash
            return ResponseEntity.ok(Result.success(user));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "User not found"));
        }
    }

    /**
     * Update user information.
     * Matches frontend: PUT /api/users/update
     */
    @PutMapping("/update")
    @Operation(summary = "Update Profile", description = "Modify user email and phone number")
    public Result<User> updateUserInfo(@RequestBody Map<String, Object> payload) {
        Integer userId = parseUserId(payload.get("userId"));
        if (userId == null) {
            return Result.error(400, "User ID is required");
        }

        User user = new User();
        user.setUserId(userId);
        user.setUsername(readPayloadText(payload, "username"));
        user.setEmail(readPayloadText(payload, "email"));
        user.setPhone(readPayloadText(payload, "phone"));
        user.setCity(readPayloadText(payload, "city"));
        user.setAvatarUrl(readPayloadText(payload, "avatarUrl"));

        if (userService.updateUser(user)) {
            User refreshed = userService.getUserById(userId);
            if (refreshed != null) {
                refreshed.setPasswordHash(null);
                return Result.success(refreshed);
            }

            user.setPasswordHash(null);
            return Result.success(user);
        } else {
            return Result.error(404, "Update failed: User might not exist");
        }
    }

    /**
     * Get riding statistics for chart display [ID: 22].
     */
    @GetMapping("/{id}/stats")
    @Operation(summary = "Get Usage Stats", description = "Get weekly riding minutes for charts")
    public Result<List<Map<String, Object>>> getStats(@PathVariable Integer id) {
        return Result.success(bookingService.getStats(id));
    }

    /**
     * get user settings (notification and privacy) [ID: 23].
     */
    @GetMapping("/{id}/settings")
    @Operation(summary = "Get User Settings", description = "Retrieve user notification and privacy settings")
    public Result<Map<String, Object>> getSettings(@PathVariable Integer id) {
        // 这里暂时返回模拟数据，实际开发中应从数据库读取
        Map<String, Object> settings = new java.util.HashMap<>();
        settings.put("notifications", true);
        settings.put("emailNotif", false);
        settings.put("location", true);
        settings.put("dataShare", true);
        settings.put("autoTopUp", false);

        return Result.success(settings);
    }

    private Integer parseUserId(Object rawValue) {
        if (rawValue == null) {
            return null;
        }

        if (rawValue instanceof Number number) {
            return number.intValue();
        }

        try {
            String value = String.valueOf(rawValue).trim();
            if (value.isEmpty()) {
                return null;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String readPayloadText(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }

        return String.valueOf(value).trim();
    }
}
