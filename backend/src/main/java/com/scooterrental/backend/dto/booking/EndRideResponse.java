package com.scooterrental.backend.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EndRideResponse {
    private Integer bookingId;
    private Integer scooterId;
    private Integer durationMinutes;
    private BigDecimal totalCost;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingStatus;
    private String scooterStatus;
}
