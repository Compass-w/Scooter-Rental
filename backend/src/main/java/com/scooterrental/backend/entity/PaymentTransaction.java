package com.scooterrental.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentTransaction {
    private Integer transactionId;
    private Integer bookingId;
    private Integer userId;
    private Integer scooterId;
    private String gatewayProvider;
    private String transactionType;
    private String chargeCategory;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String gatewayReference;
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
