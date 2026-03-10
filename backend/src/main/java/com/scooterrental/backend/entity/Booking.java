package com.scooterrental.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class Booking {
    private Integer bookingId;
    private Integer userId;
    private Integer scooterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalCost;
    private Integer durationMinutes;
    private String status; // PENDING, ACTIVE, COMPLETED, CANCELLED
    private String scooterModel; // To be displayed on electronic receipt
    private LocalDateTime createdAt;
}
