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
    private LocalDateTime plannedEndTime;
    private BigDecimal totalCost;
    private Integer durationMinutes;
    private String status; // PENDING, ACTIVE, COMPLETED, CANCELLED
    private String planType;
    private String paymentStatus;
    private String unlockStatus;
    private String unlockReference;
    private BigDecimal overtimeFeePer15Minutes;
    private BigDecimal overtimeChargeTotal;
    private BigDecimal damageChargeTotal;
    private BigDecimal electricityChargeTotal;
    private String marketCode;
    private String serviceMode;
    private String bookingChannel;
    private String pickupStoreCode;
    private String pickupStoreName;
    private String returnStoreCode;
    private String returnStoreName;
    private Integer startBatteryLevel;
    private Integer estimatedReturnBattery;
    private Integer returnBatteryLevel;
    private Boolean liabilityAccepted;
    private LocalDateTime lastReminderAt;
    private String scooterModel; // To be displayed on electronic receipt
    private LocalDateTime createdAt;
}
