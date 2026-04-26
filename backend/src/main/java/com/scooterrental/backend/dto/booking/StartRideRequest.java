package com.scooterrental.backend.dto.booking;
public class StartRideRequest {
    private Integer userId;
    private Integer scooterId;
    private String planType;
    private String scanToken;
    private String marketCode;
    private String serviceMode;
    private String bookingChannel;
    private String pickupStoreCode;
    private String pickupStoreName;
    private String returnStoreCode;
    private String returnStoreName;
    private Integer startBatteryLevel;
    private Integer estimatedReturnBattery;
    private java.math.BigDecimal electricityFeeEstimate;
    private Boolean liabilityAccepted;
    private java.math.BigDecimal overtimeFeePer15Minutes;

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

    public String getScanToken() {
        return scanToken;
    }

    public void setScanToken(String scanToken) {
        this.scanToken = scanToken;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(String serviceMode) {
        this.serviceMode = serviceMode;
    }

    public String getBookingChannel() {
        return bookingChannel;
    }

    public void setBookingChannel(String bookingChannel) {
        this.bookingChannel = bookingChannel;
    }

    public String getPickupStoreCode() {
        return pickupStoreCode;
    }

    public void setPickupStoreCode(String pickupStoreCode) {
        this.pickupStoreCode = pickupStoreCode;
    }

    public String getPickupStoreName() {
        return pickupStoreName;
    }

    public void setPickupStoreName(String pickupStoreName) {
        this.pickupStoreName = pickupStoreName;
    }

    public String getReturnStoreCode() {
        return returnStoreCode;
    }

    public void setReturnStoreCode(String returnStoreCode) {
        this.returnStoreCode = returnStoreCode;
    }

    public String getReturnStoreName() {
        return returnStoreName;
    }

    public void setReturnStoreName(String returnStoreName) {
        this.returnStoreName = returnStoreName;
    }

    public Integer getStartBatteryLevel() {
        return startBatteryLevel;
    }

    public void setStartBatteryLevel(Integer startBatteryLevel) {
        this.startBatteryLevel = startBatteryLevel;
    }

    public Integer getEstimatedReturnBattery() {
        return estimatedReturnBattery;
    }

    public void setEstimatedReturnBattery(Integer estimatedReturnBattery) {
        this.estimatedReturnBattery = estimatedReturnBattery;
    }

    public java.math.BigDecimal getElectricityFeeEstimate() {
        return electricityFeeEstimate;
    }

    public void setElectricityFeeEstimate(java.math.BigDecimal electricityFeeEstimate) {
        this.electricityFeeEstimate = electricityFeeEstimate;
    }

    public Boolean getLiabilityAccepted() {
        return liabilityAccepted;
    }

    public void setLiabilityAccepted(Boolean liabilityAccepted) {
        this.liabilityAccepted = liabilityAccepted;
    }

    public java.math.BigDecimal getOvertimeFeePer15Minutes() {
        return overtimeFeePer15Minutes;
    }

    public void setOvertimeFeePer15Minutes(java.math.BigDecimal overtimeFeePer15Minutes) {
        this.overtimeFeePer15Minutes = overtimeFeePer15Minutes;
    }
}
