package com.scooterrental.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleUnlockSession {
    private Integer commandId;
    private Integer bookingId;
    private Integer userId;
    private Integer scooterId;
    private String scanToken;
    private String commandStatus;
    private String communicationStatus;
    private String telemetryStatus;
    private String lockState;
    private String deviceMessage;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime lastHeartbeatAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
