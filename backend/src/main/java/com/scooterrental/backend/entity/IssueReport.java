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
    private String status;
    private String priority;
    private String workflowStatus;
    private String assignedStaff;
    private String safetyAction;
    private String insuranceCaseStatus;
    private String customerChargePolicy;
    private java.math.BigDecimal repairChargeEstimate;
    private Boolean riderInjured;
    private Boolean thirdPartyInvolved;
    private Boolean emergencyServicesContacted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
