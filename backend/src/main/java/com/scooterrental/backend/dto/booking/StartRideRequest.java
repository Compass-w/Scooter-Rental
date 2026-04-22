package com.scooterrental.backend.dto.booking;
public class StartRideRequest {
    private Integer userId;
    private Integer scooterId;
    private String planType;
    private String scanToken;
    private String marketCode;
    private String serviceMode;
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

    public java.math.BigDecimal getOvertimeFeePer15Minutes() {
        return overtimeFeePer15Minutes;
    }

    public void setOvertimeFeePer15Minutes(java.math.BigDecimal overtimeFeePer15Minutes) {
        this.overtimeFeePer15Minutes = overtimeFeePer15Minutes;
    }
}
