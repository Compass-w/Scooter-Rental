package com.scooterrental.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StaffBooking {
    private Integer bookingId;
    private String guestName;
    private String guestEmail;
    private Integer scooterId;
    private String scooterModel;
    private String hirePeriod;
    private LocalDateTime desiredStartTime;
    private BigDecimal estimatedCost;
    private String bookingStatus;
    private LocalDateTime confirmationSentAt;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
}
