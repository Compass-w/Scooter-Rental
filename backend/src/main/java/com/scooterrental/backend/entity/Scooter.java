package com.scooterrental.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Scooter Entity corresponding to the 'scooters' table.
 */
@Data
@TableName("scooters")
public class Scooter {

    /**
     * Primary Key.
     * IdType.AUTO works with PostgreSQL 'SERIAL' columns.
     * The JDBC driver handles the key retrieval automatically.
     */
    @TableId(type = IdType.AUTO)
    private Long scooterId;

    private String model;

    // GPS Coordinates
    private Double latitude;
    private Double longitude;

    private Integer batteryLevel;

    // Status: AVAILABLE, IN_USE, MAINTENANCE, LOW_BATTERY
    private String status;

    // Pricing fields
    private BigDecimal basePrice;
    private BigDecimal pricePerMin;
}
