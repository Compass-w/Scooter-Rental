package com.scooterrental.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "User Entity Info")
public class User {

    @Schema(description = "User Unique ID", example = "1")
    private Integer userId;

    @Schema(description = "Username for login", example = "admin")
    private String username;

    @Schema(description = "User Email", example = "admin@example.com")
    private String email;

    @Schema(description = "Encrypted Password", hidden = true)
    private String passwordHash;

    @Schema(description = "User Role", example = "manager", allowableValues = { "customer", "manager" })
    private String role;

    @Schema(description = "Registration Time")
    private LocalDateTime createdAt;
}
