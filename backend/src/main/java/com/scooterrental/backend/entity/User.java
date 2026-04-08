package com.scooterrental.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "User Entity Info")
public class User {

    @Schema(description = "User Unique ID", example = "1")
    private Integer userId;

    @Schema(description = "Username for login", example = "student1")
    private String username;

    @Schema(description = "User Email", example = "student1@leeds.ac.uk")
    private String email;

    @Schema(description = "Phone Number", example = "07123456789")
    private String phone;

    @Schema(description = "City of residence", example = "Leeds")
    private String city;

    @Schema(description = "Encrypted Password", hidden = true)
    private String passwordHash;

    @Schema(description = "User Role", example = "customer", allowableValues = { "customer", "manager" })
    private String role;

    // --- Added for Sprint 1 Distinction Features ---
    @Schema(description = "Total riding time in minutes", example = "120")
    private Integer totalRidingMinutes;

    @Schema(description = "User achievements (comma separated)", example = "Eco-Warrior,First Ride")
    private String achievements;

    @Schema(description = "Registration Time")
    private LocalDateTime createdAt;
}
