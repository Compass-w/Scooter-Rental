package com.scooterrental.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceLog {
    private Integer logId;
    private Integer scooterId;
    private String technicianName;
    private String actionTaken;
    private String notes;
    private Integer batteryLevel;
    private LocalDateTime createdAt;
}
