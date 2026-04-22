package com.scooterrental.backend.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EndRideResponse {
    private Integer bookingId;
    private Integer scooterId;
    private Integer durationMinutes;
    private BigDecimal baseCost;
    private BigDecimal overtimeChargeTotal;
    private BigDecimal damageChargeTotal;
    private BigDecimal totalCost;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingStatus;
    private String paymentStatus;
    private String scooterStatus;

    public EndRideResponse(
            Integer bookingId,
            Integer scooterId,
            Integer durationMinutes,
            BigDecimal baseCost,
            BigDecimal overtimeChargeTotal,
            BigDecimal damageChargeTotal,
            BigDecimal totalCost,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String bookingStatus,
            String paymentStatus,
            String scooterStatus) {
        this.bookingId = bookingId;
        this.scooterId = scooterId;
        this.durationMinutes = durationMinutes;
        this.baseCost = baseCost;
        this.overtimeChargeTotal = overtimeChargeTotal;
        this.damageChargeTotal = damageChargeTotal;
        this.totalCost = totalCost;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.scooterStatus = scooterStatus;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getScooterId() {
        return scooterId;
    }

    public void setScooterId(Integer scooterId) {
        this.scooterId = scooterId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public BigDecimal getOvertimeChargeTotal() {
        return overtimeChargeTotal;
    }

    public void setOvertimeChargeTotal(BigDecimal overtimeChargeTotal) {
        this.overtimeChargeTotal = overtimeChargeTotal;
    }

    public BigDecimal getDamageChargeTotal() {
        return damageChargeTotal;
    }

    public void setDamageChargeTotal(BigDecimal damageChargeTotal) {
        this.damageChargeTotal = damageChargeTotal;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

    public String getScooterStatus() {
        return scooterStatus;
    }

    public void setScooterStatus(String scooterStatus) {
        this.scooterStatus = scooterStatus;
    }
}
