package com.scooterrental.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
@Tag(name = "Booking Module", description = "Handle rentals and payments")
public class BookingController {

    // 开始租车
    @PostMapping("/start")
    @Operation(summary = "Start Ride", description = "User scans QR code to start rental")
    public Map<String, Object> startRide(@RequestBody Map<String, Integer> request) {
        // 前端传过来: {"userId": 1, "scooterId": 101}
        return Map.of(
                "code", 200,
                "msg", "Ride started",
                "bookingId", 5001 // 返回一个假订单号
        );
    }

    // 结束租车
    @PostMapping("/end")
    @Operation(summary = "End Ride", description = "User ends rental and pays")
    public Map<String, Object> endRide(@RequestBody Map<String, Integer> request) {
        return Map.of(
                "code", 200,
                "msg", "Ride ended",
                "cost", 15.50);
    }
}
