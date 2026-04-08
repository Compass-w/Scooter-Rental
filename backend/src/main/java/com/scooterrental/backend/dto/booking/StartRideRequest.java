package com.scooterrental.backend.dto.booking;

import lombok.Data;

@Data
public class StartRideRequest {
    private Integer userId;
    private Integer scooterId;
    private String planType;
}
