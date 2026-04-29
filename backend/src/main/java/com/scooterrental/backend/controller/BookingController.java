package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.dto.booking.EndRideResponse;
import com.scooterrental.backend.dto.booking.StartRideRequest;
import com.scooterrental.backend.dto.booking.StartRideResponse;
import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.security.AuthAccess;
import com.scooterrental.backend.service.AuthSessionService;
import com.scooterrental.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Module", description = "Handle rentals and payments")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // --- Match frontend: getBookingHistory ---
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get Booking History", description = "List all past bookings for a user [ID: 8]")
    public Result<List<Booking>> getHistory(
            @PathVariable Integer userId,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        AuthAccess.requireSelfOrStaff(session, userId);
        return Result.success(bookingService.getHistory(userId));
    }

    // --- Match frontend: cancelBooking ---
    @PostMapping("/cancel")
    @Operation(summary = "Cancel Booking", description = "Cancel a ride that hasn't started [ID: 12]")
    public Result<String> cancelBooking(
            @RequestBody Map<String, Integer> params,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        Integer bookingId = params.get("bookingId");
        Integer userId = params.get("userId");
        if (!AuthAccess.isStaff(session)) {
            userId = AuthAccess.requireAuthenticatedUserId(session);
        }
        AuthAccess.requireSelfOrStaff(session, userId);
        if (bookingService.cancel(bookingId, userId)) {
            return Result.success("Booking cancelled successfully");
        }
        return Result.error(400, "Cannot cancel: Booking not found or already in progress");
    }

    @PostMapping("/start")
    @Operation(summary = "Start Ride", description = "User scans QR code to start rental")
    public ResponseEntity<Result<StartRideResponse>> startRide(
            @RequestBody StartRideRequest request,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        try {
            if (request != null && !AuthAccess.isStaff(session)) {
                request.setUserId(AuthAccess.requireAuthenticatedUserId(session));
            } else if (request != null) {
                AuthAccess.requireSelfOrStaff(session, request.getUserId());
            }
            return ResponseEntity.ok(Result.success(bookingService.startRide(request)));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, ex.getMessage()));
        }
    }

    @PostMapping("/{bookingId}/end")
    @Operation(summary = "End Ride", description = "User ends rental and pays")
    public ResponseEntity<Result<EndRideResponse>> endRide(
            @PathVariable Integer bookingId,
            @RequestBody(required = false) Map<String, Object> payload,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        try {
            requireBookingAccess(bookingId, session);
            return ResponseEntity.ok(Result.success(bookingService.endRide(bookingId, payload)));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, ex.getMessage()));
        }
    }

    @PostMapping("/end")
    @Operation(summary = "End Ride (Legacy)", description = "Compatibility route that accepts bookingId in the request body")
    public ResponseEntity<Result<EndRideResponse>> endRideLegacy(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        Integer bookingId = parseInteger(request == null ? null : request.get("bookingId"));
        if (bookingId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Booking ID is required"));
        }
        return endRide(bookingId, request, session);
    }

    private Integer parseInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @PostMapping("/{bookingId}/extend")
    @Operation(summary = "Extend Ride", description = "Add more time to an active booking")
    public ResponseEntity<Result<Map<String, Object>>> extendRide(
            @PathVariable Integer bookingId,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal AuthSessionService.AuthSession session
    ) {
        try {
            requireBookingAccess(bookingId, session);
            return ResponseEntity.ok(Result.success(bookingService.extendRide(bookingId, request.get("extraMinutes"))));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, ex.getMessage()));
        }
    }

    @PostMapping("/{bookingId}/telemetry")
    @Operation(summary = "Record ride telemetry", description = "Receive ride heartbeat, battery, and location updates from the unlocked scooter session")
    public ResponseEntity<Result<Map<String, Object>>> recordTelemetry(
            @PathVariable Integer bookingId,
            @RequestBody(required = false) Map<String, Object> payload,
            @AuthenticationPrincipal AuthSessionService.AuthSession session
    ) {
        try {
            requireBookingAccess(bookingId, session);
            return ResponseEntity.ok(Result.success(bookingService.recordTelemetry(bookingId, payload)));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, ex.getMessage()));
        }
    }

    private void requireBookingAccess(Integer bookingId, AuthSessionService.AuthSession session) {
        AuthAccess.requireAuthenticatedUserId(session);
        if (!AuthAccess.isStaff(session) && !bookingService.isBookingOwnedBy(bookingId, session.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own bookings");
        }
    }
}
