package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.entity.Booking; // Assuming this exists from previous turns
import com.scooterrental.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
@Tag(name = "Booking Module", description = "Handle rentals and payments")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // --- Match frontend: getBookingHistory ---
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get Booking History", description = "List all past bookings for a user [ID: 8]")
    public Result<List<Booking>> getHistory(@PathVariable Integer userId) {
        return Result.success(bookingService.getHistory(userId));
    }

    // --- Match frontend: cancelBooking ---
    @PostMapping("/cancel")
    @Operation(summary = "Cancel Booking", description = "Cancel a ride that hasn't started [ID: 12]")
    public Result<String> cancelBooking(@RequestBody Map<String, Integer> params) {
        Integer bookingId = params.get("bookingId");
        Integer userId = params.get("userId");
        if (bookingService.cancel(bookingId, userId)) {
            return Result.success("Booking cancelled successfully");
        }
        return Result.error(400, "Cannot cancel: Booking not found or already in progress");
    }

    @PostMapping("/start")
    @Operation(summary = "Start Ride", description = "User scans QR code to start rental")
    public Map<String, Object> startRide(@RequestBody Map<String, Integer> request) {
        return Map.of("code", 200, "msg", "Ride started", "bookingId", 5001);
    }

    @PostMapping("/end")
    @Operation(summary = "End Ride", description = "User ends rental and pays")
    public Map<String, Object> endRide(@RequestBody Map<String, Integer> request) {
        return Map.of("code", 200, "msg", "Ride ended", "cost", 15.50);
    }
}
