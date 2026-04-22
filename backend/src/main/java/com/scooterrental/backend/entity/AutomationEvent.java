package com.scooterrental.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AutomationEvent {
    private Integer eventId;
    private Integer bookingId;
    private Integer issueId;
    private String eventType;
    private String status;
    private BigDecimal amount;
    private String detail;
    private LocalDateTime dueAt;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}
