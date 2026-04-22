package com.scooterrental.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OpsAssignment {
    private Integer assignmentId;
    private String staffName;
    private String staffRole;
    private String zoneLabel;
    private String shiftStatus;
    private Integer tasksInQueue;
    private Integer assignedScooters;
    private String contactPhone;
    private String notes;
    private LocalDateTime lastSeenAt;
    private LocalDateTime createdAt;
}
