package com.scooterrental.backend.dto.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncomeReportPoint {
    private String label;
    private BigDecimal revenue;
    private Integer bookingCount;
}
