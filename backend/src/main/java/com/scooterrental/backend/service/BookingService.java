package com.scooterrental.backend.service;

import com.scooterrental.backend.dto.booking.EndRideResponse;
import com.scooterrental.backend.dto.booking.StartRideRequest;
import com.scooterrental.backend.dto.booking.StartRideResponse;
import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.mapper.BookingMapper;
import com.scooterrental.backend.mapper.ScooterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    private static final String BOOKING_ACTIVE = "ACTIVE";
    private static final String BOOKING_COMPLETED = "COMPLETED";
    private static final String SCOOTER_AVAILABLE = "AVAILABLE";
    private static final String SCOOTER_IN_USE = "IN_USE";

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private ScooterMapper scooterMapper;

    public List<Booking> getHistory(Integer userId) {
        return bookingMapper.selectByUserId(userId);
    }

    public boolean cancel(Integer bookingId, Integer userId) {
        return bookingMapper.cancelBooking(bookingId, userId) > 0;
    }

    public List<Map<String, Object>> getStats(Integer userId) {
        return bookingMapper.getWeeklyStats(userId);
    }

    @Transactional
    public StartRideResponse startRide(StartRideRequest request) {
        validateStartRideRequest(request);

        Scooter scooter = scooterMapper.selectById(request.getScooterId());
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }
        if (!SCOOTER_AVAILABLE.equalsIgnoreCase(scooter.getStatus())) {
            throw new IllegalStateException("Scooter is not available");
        }

        String normalizedPlanType = normalizePlanType(request.getPlanType());
        Integer durationMinutes = resolveDurationMinutes(normalizedPlanType);
        BigDecimal estimatedCost = calculateCost(scooter, durationMinutes);

        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setScooterId(request.getScooterId());
        booking.setStartTime(LocalDateTime.now());
        booking.setDurationMinutes(durationMinutes);
        booking.setTotalCost(estimatedCost);
        booking.setStatus(BOOKING_ACTIVE);

        bookingMapper.insertBooking(booking);

        scooter.setStatus(SCOOTER_IN_USE);
        scooterMapper.updateById(scooter);

        return new StartRideResponse(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getScooterId(),
                normalizedPlanType,
                durationMinutes,
                estimatedCost,
                booking.getStatus(),
                scooter.getStatus());
    }

    @Transactional
    public EndRideResponse endRide(Integer bookingId) {
        Booking booking = bookingMapper.selectByBookingId(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        if (!BOOKING_ACTIVE.equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking is not currently active");
        }

        Scooter scooter = scooterMapper.selectById(booking.getScooterId());
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }

        LocalDateTime endTime = LocalDateTime.now();
        int actualMinutes = Math.max(1, (int) Duration.between(booking.getStartTime(), endTime).toMinutes());
        int billableMinutes = booking.getDurationMinutes() != null && booking.getDurationMinutes() > 0
                ? booking.getDurationMinutes()
                : actualMinutes;
        BigDecimal finalCost = calculateCost(scooter, billableMinutes);

        booking.setEndTime(endTime);
        booking.setDurationMinutes(billableMinutes);
        booking.setTotalCost(finalCost);
        booking.setStatus(BOOKING_COMPLETED);
        bookingMapper.completeBooking(booking);

        scooter.setStatus(SCOOTER_AVAILABLE);
        scooterMapper.updateById(scooter);

        return new EndRideResponse(
                booking.getBookingId(),
                booking.getScooterId(),
                booking.getDurationMinutes(),
                booking.getTotalCost(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                scooter.getStatus());
    }

    @Transactional
    public Map<String, Object> extendRide(Integer bookingId, Integer extraMinutes) {
        if (extraMinutes == null || extraMinutes <= 0) {
            throw new IllegalArgumentException("Extension time must be greater than 0 minutes");
        }

        Booking booking = bookingMapper.selectByBookingId(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        if (!BOOKING_ACTIVE.equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking is not currently active");
        }

        Scooter scooter = scooterMapper.selectById(booking.getScooterId());
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }

        int currentDuration = booking.getDurationMinutes() != null ? booking.getDurationMinutes() : 0;
        int nextDuration = currentDuration + extraMinutes;
        BigDecimal updatedCost = calculateCost(scooter, nextDuration);

        booking.setDurationMinutes(nextDuration);
        booking.setTotalCost(updatedCost);
        bookingMapper.extendActiveBooking(booking);

        Map<String, Object> response = new HashMap<>();
        response.put("bookingId", booking.getBookingId());
        response.put("scooterId", booking.getScooterId());
        response.put("addedMinutes", extraMinutes);
        response.put("durationMinutes", booking.getDurationMinutes());
        response.put("totalCost", booking.getTotalCost());
        response.put("plannedEndTime", booking.getStartTime().plusMinutes(booking.getDurationMinutes()));
        response.put("bookingStatus", booking.getStatus());
        return response;
    }

    private void validateStartRideRequest(StartRideRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getScooterId() == null) {
            throw new IllegalArgumentException("Scooter ID is required");
        }
    }

    private String normalizePlanType(String planType) {
        if (planType == null || planType.isBlank()) {
            return "PAY_AS_YOU_GO";
        }

        String normalized = planType.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "1_HOUR", "1HR" -> "1_HOUR";
            case "4_HOURS", "4_HOUR", "4HRS", "4HR" -> "4_HOURS";
            case "1_DAY", "1DAY" -> "1_DAY";
            case "1_WEEK", "1WEEK" -> "1_WEEK";
            case "PAY_AS_YOU_GO" -> "PAY_AS_YOU_GO";
            default -> throw new IllegalArgumentException("Unsupported hire period: " + planType);
        };
    }

    private Integer resolveDurationMinutes(String planType) {
        return switch (planType) {
            case "1_HOUR" -> 60;
            case "4_HOURS" -> 240;
            case "1_DAY" -> 1440;
            case "1_WEEK" -> 10080;
            case "PAY_AS_YOU_GO" -> null;
            default -> throw new IllegalArgumentException("Unsupported hire period: " + planType);
        };
    }

    private BigDecimal calculateCost(Scooter scooter, Integer durationMinutes) {
        BigDecimal basePrice = scooter.getBasePrice() != null ? scooter.getBasePrice() : BigDecimal.ZERO;
        if (durationMinutes == null) {
            return basePrice.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal pricePerMinute = scooter.getPricePerMin() != null ? scooter.getPricePerMin() : BigDecimal.ZERO;
        return basePrice
                .add(pricePerMinute.multiply(BigDecimal.valueOf(durationMinutes)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
