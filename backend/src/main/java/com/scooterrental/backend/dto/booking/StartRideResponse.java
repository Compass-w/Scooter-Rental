package com.scooterrental.backend.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StartRideResponse {
    private Integer bookingId;
    private Integer userId;
    private Integer scooterId;
    private String planType;
    private Integer durationMinutes;
    private BigDecimal estimatedCost;
    private String bookingStatus;
    private String paymentStatus;
    private String unlockStatus;
    private String unlockReference;
    private String deviceMessage;
    private String scooterStatus;
    private LocalDateTime plannedEndTime;

    public StartRideResponse(
            Integer bookingId,
            Integer userId,
            Integer scooterId,
            String planType,
            Integer durationMinutes,
            BigDecimal estimatedCost,
            String bookingStatus,
            String paymentStatus,
            String unlockStatus,
            String unlockReference,
            String deviceMessage,
            String scooterStatus,
            LocalDateTime plannedEndTime) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.scooterId = scooterId;
        this.planType = planType;
        this.durationMinutes = durationMinutes;
        this.estimatedCost = estimatedCost;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.unlockStatus = unlockStatus;
        this.unlockReference = unlockReference;
        this.deviceMessage = deviceMessage;
        this.scooterStatus = scooterStatus;
        this.plannedEndTime = plannedEndTime;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getUnlockStatus() {
        return unlockStatus;
    }

    public void setUnlockStatus(String unlockStatus) {
        this.unlockStatus = unlockStatus;
    }

    public String getUnlockReference() {
        return unlockReference;
    }

    public void setUnlockReference(String unlockReference) {
        this.unlockReference = unlockReference;
    }

    public String getDeviceMessage() {
        return deviceMessage;
    }

    public void setDeviceMessage(String deviceMessage) {
        this.deviceMessage = deviceMessage;
    }

    public String getScooterStatus() {
        return scooterStatus;
    }

    public void setScooterStatus(String scooterStatus) {
        this.scooterStatus = scooterStatus;
    }

    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }
}
