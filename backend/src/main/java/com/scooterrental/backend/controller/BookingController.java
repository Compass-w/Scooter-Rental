package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result; // Required for Result type
import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired; // Required for @Autowired
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
@Tag(name = "Booking Module", description = "Handle rentals and payments")
public class BookingController {

    @Autowired // This was causing the "Autowired cannot be resolved" error
    private BookingService bookingService;

    // Get Booking History [ID: 8]
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get Booking History", description = "List all past and current bookings")
    public Result<List<Booking>> getHistory(@PathVariable Integer userId) {
        return Result.success(bookingService.getHistory(userId));
    }

    // Cancel Booking [ID: 12]
    @PostMapping("/cancel")
    @Operation(summary = "Cancel Booking", description = "Cancel a ride that hasn't started [ID: 12]")
    public Result<String> cancelBooking(@RequestBody Map<String, Integer> params) {
        Integer bookingId = params.get("bookingId");
        Integer userId = params.get("userId");

        // Logic: Only PENDING bookings can be cancelled
        boolean success = bookingService.cancel(bookingId, userId);

        if (success) {
            return Result.success("Booking cancelled successfully");
        } else {
            return Result.error(400, "Cannot cancel: Booking not found or already started/completed");
        }
    }
}
