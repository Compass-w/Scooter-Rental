package com.scooterrental.backend.dto.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IncomeReportResponse {
    private String type;
    private String periodLabel;
    private Integer periodCount;
    private BigDecimal totalRevenue;
    private Integer totalBookings;
    private List<IncomeReportPoint> points;
}
