package com.scooterrental.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssueReport {
    private Integer issueId;
    private Integer userId;
    private Integer scooterId;
    private Integer bookingId;
    private String category;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
