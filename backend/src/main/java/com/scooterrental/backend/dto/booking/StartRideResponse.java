package com.scooterrental.backend.dto.booking;

import java.math.BigDecimal;

public class StartRideResponse {
    private Integer bookingId;
    private Integer userId;
    private Integer scooterId;
    private String planType;
    private Integer durationMinutes;
    private BigDecimal estimatedCost;
    private String bookingStatus;
    private String scooterStatus;

    public StartRideResponse(
            Integer bookingId,
            Integer userId,
            Integer scooterId,
            String planType,
            Integer durationMinutes,
            BigDecimal estimatedCost,
            String bookingStatus,
            String scooterStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.scooterId = scooterId;
        this.planType = planType;
        this.durationMinutes = durationMinutes;
        this.estimatedCost = estimatedCost;
        this.bookingStatus = bookingStatus;
        this.scooterStatus = scooterStatus;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScooterId() {
        return scooterId;
    }

    public void setScooterId(Integer scooterId) {
        this.scooterId = scooterId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getScooterStatus() {
        return scooterStatus;
    }

    public void setScooterStatus(String scooterStatus) {
        this.scooterStatus = scooterStatus;
    }
}
