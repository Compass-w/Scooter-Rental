package com.scooterrental.backend.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StartRideResponse {
    private Integer bookingId;
    private Integer userId;
    private Integer scooterId;
    private String planType;
    private Integer durationMinutes;
    private BigDecimal estimatedCost;
    private String bookingStatus;
    private String scooterStatus;
}
