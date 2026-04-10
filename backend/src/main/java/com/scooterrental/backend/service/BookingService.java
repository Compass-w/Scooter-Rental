package com.scooterrental.backend.service;

import com.scooterrental.backend.dto.admin.IncomeReportPoint;
import com.scooterrental.backend.dto.admin.IncomeReportResponse;
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
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class BookingService {

    private static final String BOOKING_ACTIVE = "ACTIVE";
    private static final String BOOKING_COMPLETED = "COMPLETED";
    private static final String SCOOTER_AVAILABLE = "AVAILABLE";
    private static final String SCOOTER_IN_USE = "IN_USE";
    private static final String REPORT_DAILY = "DAILY";
    private static final String REPORT_WEEKLY = "WEEKLY";
    private static final String PLAN_PAY_AS_YOU_GO = "PAY_AS_YOU_GO";
    private static final String PLAN_ONE_HOUR = "1_HOUR";
    private static final String PLAN_FOUR_HOURS = "4_HOURS";
    private static final String PLAN_ONE_DAY = "1_DAY";
    private static final String PLAN_ONE_WEEK = "1_WEEK";
    private static final String PLAN_ONE_MONTH = "1_MONTH";
    private static final int MINUTES_ONE_HOUR = 60;
    private static final int MINUTES_FOUR_HOURS = 240;
    private static final int MINUTES_ONE_DAY = 1440;
    private static final int MINUTES_ONE_WEEK = 10080;
    private static final int MINUTES_ONE_MONTH = 43200;
    private static final BigDecimal PRICE_ONE_HOUR = BigDecimal.valueOf(3.50);
    private static final BigDecimal PRICE_FOUR_HOURS = BigDecimal.valueOf(12.00);
    private static final BigDecimal PRICE_ONE_DAY = BigDecimal.valueOf(30.00);
    private static final BigDecimal PRICE_ONE_WEEK = BigDecimal.valueOf(100.00);
    private static final BigDecimal PRICE_ONE_MONTH = BigDecimal.valueOf(280.00);
    private static final DateTimeFormatter REPORT_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH);
    private static final DateTimeFormatter REPORT_KEY_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

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

    public IncomeReportResponse getIncomeReport(String type) {
        String normalizedType = normalizeReportType(type);
        return REPORT_WEEKLY.equals(normalizedType) ? buildWeeklyIncomeReport() : buildDailyIncomeReport();
    }

    @Transactional
    public StartRideResponse startRide(StartRideRequest request) {
        validateStartRideRequest(request);

        Booking existingActiveBooking = bookingMapper.selectActiveBookingByUserId(request.getUserId());
        if (existingActiveBooking != null) {
            throw new IllegalStateException(
                    "You already have an active ride. Please end booking #" + existingActiveBooking.getBookingId() + " before starting a new one.");
        }

        Scooter scooter = scooterMapper.selectById(request.getScooterId());
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }
        if (!SCOOTER_AVAILABLE.equalsIgnoreCase(scooter.getStatus())) {
            throw new IllegalStateException("Scooter is not available");
        }

        String normalizedPlanType = normalizePlanType(request.getPlanType());
        Integer durationMinutes = resolveDurationMinutes(normalizedPlanType);
        BigDecimal estimatedCost = calculateCost(scooter, normalizedPlanType, durationMinutes);

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
        BigDecimal finalCost = calculateCost(scooter, null, billableMinutes);

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
        BigDecimal updatedCost = calculateCost(scooter, null, nextDuration);

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
            return PLAN_PAY_AS_YOU_GO;
        }

        String normalized = planType.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "1_HOUR", "1HR" -> PLAN_ONE_HOUR;
            case "4_HOURS", "4_HOUR", "4HRS", "4HR" -> PLAN_FOUR_HOURS;
            case "1_DAY", "1DAY" -> PLAN_ONE_DAY;
            case "1_WEEK", "1WEEK" -> PLAN_ONE_WEEK;
            case "1_MONTH", "1MONTH", "MONTHLY", "1MO" -> PLAN_ONE_MONTH;
            case "PAY_AS_YOU_GO" -> PLAN_PAY_AS_YOU_GO;
            default -> throw new IllegalArgumentException("Unsupported hire period: " + planType);
        };
    }

    private Integer resolveDurationMinutes(String planType) {
        return switch (planType) {
            case PLAN_ONE_HOUR -> MINUTES_ONE_HOUR;
            case PLAN_FOUR_HOURS -> MINUTES_FOUR_HOURS;
            case PLAN_ONE_DAY -> MINUTES_ONE_DAY;
            case PLAN_ONE_WEEK -> MINUTES_ONE_WEEK;
            case PLAN_ONE_MONTH -> MINUTES_ONE_MONTH;
            case PLAN_PAY_AS_YOU_GO -> null;
            default -> throw new IllegalArgumentException("Unsupported hire period: " + planType);
        };
    }

    private BigDecimal calculateCost(Scooter scooter, String planType, Integer durationMinutes) {
        BigDecimal basePrice = scooter.getBasePrice() != null ? scooter.getBasePrice() : BigDecimal.ZERO;
        if (durationMinutes == null) {
            return basePrice.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal packagePrice = resolvePackagePrice(planType, durationMinutes);
        if (packagePrice != null) {
            return packagePrice.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal pricePerMinute = scooter.getPricePerMin() != null ? scooter.getPricePerMin() : BigDecimal.ZERO;
        BigDecimal matchedPackagePrice = resolvePackagePriceByDuration(durationMinutes);
        if (matchedPackagePrice != null) {
          return matchedPackagePrice.setScale(2, RoundingMode.HALF_UP);
        }

        PackageTier packageTier = resolvePackageTier(durationMinutes);
        if (packageTier != null) {
            int extraMinutes = durationMinutes - packageTier.durationMinutes();
            return packageTier.price()
                    .add(pricePerMinute.multiply(BigDecimal.valueOf(extraMinutes)))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return basePrice
                .add(pricePerMinute.multiply(BigDecimal.valueOf(durationMinutes)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolvePackagePrice(String planType, Integer durationMinutes) {
        if (planType == null || durationMinutes == null) {
            return null;
        }

        return switch (planType) {
            case PLAN_ONE_HOUR -> PRICE_ONE_HOUR;
            case PLAN_FOUR_HOURS -> PRICE_FOUR_HOURS;
            case PLAN_ONE_DAY -> PRICE_ONE_DAY;
            case PLAN_ONE_WEEK -> PRICE_ONE_WEEK;
            case PLAN_ONE_MONTH -> PRICE_ONE_MONTH;
            default -> resolvePackagePriceByDuration(durationMinutes);
        };
    }

    private BigDecimal resolvePackagePriceByDuration(Integer durationMinutes) {
        if (durationMinutes == null) {
            return null;
        }

        return switch (durationMinutes) {
            case MINUTES_ONE_HOUR -> PRICE_ONE_HOUR;
            case MINUTES_FOUR_HOURS -> PRICE_FOUR_HOURS;
            case MINUTES_ONE_DAY -> PRICE_ONE_DAY;
            case MINUTES_ONE_WEEK -> PRICE_ONE_WEEK;
            case MINUTES_ONE_MONTH -> PRICE_ONE_MONTH;
            default -> null;
        };
    }

    private IncomeReportResponse buildDailyIncomeReport() {
        int dayCount = 7;
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(dayCount - 1L);
        List<IncomeReportPoint> rawPoints = bookingMapper.selectRevenueByDay(startDate.atStartOfDay());
        Map<String, IncomeReportPoint> pointByDate = new HashMap<>();
        for (IncomeReportPoint point : rawPoints) {
            pointByDate.put(point.getLabel(), point);
        }

        List<IncomeReportPoint> points = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalBookings = 0;

        for (int i = 0; i < dayCount; i++) {
            LocalDate date = startDate.plusDays(i);
            IncomeReportPoint point = pointByDate.get(date.format(REPORT_KEY_FORMATTER));
            IncomeReportPoint normalizedPoint = new IncomeReportPoint();
            normalizedPoint.setLabel(date.format(REPORT_LABEL_FORMATTER));
            normalizedPoint.setRevenue(normalizeRevenue(point == null ? null : point.getRevenue()));
            normalizedPoint.setBookingCount(point == null || point.getBookingCount() == null ? 0 : point.getBookingCount());
            points.add(normalizedPoint);
            totalRevenue = totalRevenue.add(normalizedPoint.getRevenue());
            totalBookings += normalizedPoint.getBookingCount();
        }

        IncomeReportResponse response = new IncomeReportResponse();
        response.setType(REPORT_DAILY);
        response.setPeriodLabel("Last 7 Days");
        response.setPeriodCount(dayCount);
        response.setTotalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_UP));
        response.setTotalBookings(totalBookings);
        response.setPoints(points);
        return response;
    }

    private IncomeReportResponse buildWeeklyIncomeReport() {
        int weekCount = 6;
        LocalDate currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startWeek = currentWeekStart.minusWeeks(weekCount - 1L);
        List<IncomeReportPoint> rawPoints = bookingMapper.selectRevenueByWeek(startWeek.atStartOfDay());
        Map<String, IncomeReportPoint> pointByWeek = new HashMap<>();
        for (IncomeReportPoint point : rawPoints) {
            pointByWeek.put(point.getLabel(), point);
        }

        List<IncomeReportPoint> points = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalBookings = 0;

        for (int i = 0; i < weekCount; i++) {
            LocalDate weekStart = startWeek.plusWeeks(i);
            IncomeReportPoint point = pointByWeek.get(weekStart.format(REPORT_KEY_FORMATTER));
            IncomeReportPoint normalizedPoint = new IncomeReportPoint();
            normalizedPoint.setLabel("Week of " + weekStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " " + String.format(Locale.ENGLISH, "%02d", weekStart.getDayOfMonth()));
            normalizedPoint.setRevenue(normalizeRevenue(point == null ? null : point.getRevenue()));
            normalizedPoint.setBookingCount(point == null || point.getBookingCount() == null ? 0 : point.getBookingCount());
            points.add(normalizedPoint);
            totalRevenue = totalRevenue.add(normalizedPoint.getRevenue());
            totalBookings += normalizedPoint.getBookingCount();
        }

        IncomeReportResponse response = new IncomeReportResponse();
        response.setType(REPORT_WEEKLY);
        response.setPeriodLabel("Last 6 Weeks");
        response.setPeriodCount(weekCount);
        response.setTotalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_UP));
        response.setTotalBookings(totalBookings);
        response.setPoints(points);
        return response;
    }

    private String normalizeReportType(String type) {
        if (type == null || type.isBlank()) {
            return REPORT_DAILY;
        }

        return switch (type.trim().toUpperCase(Locale.ROOT)) {
            case REPORT_DAILY -> REPORT_DAILY;
            case REPORT_WEEKLY -> REPORT_WEEKLY;
            default -> throw new IllegalArgumentException("Unsupported report type: " + type);
        };
    }

    private BigDecimal normalizeRevenue(BigDecimal revenue) {
        return (revenue == null ? BigDecimal.ZERO : revenue).setScale(2, RoundingMode.HALF_UP);
    }

    private PackageTier resolvePackageTier(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            return null;
        }

        if (durationMinutes >= MINUTES_ONE_MONTH) {
            return new PackageTier(MINUTES_ONE_MONTH, PRICE_ONE_MONTH);
        }
        if (durationMinutes >= MINUTES_ONE_WEEK) {
            return new PackageTier(MINUTES_ONE_WEEK, PRICE_ONE_WEEK);
        }
        if (durationMinutes >= MINUTES_ONE_DAY) {
            return new PackageTier(MINUTES_ONE_DAY, PRICE_ONE_DAY);
        }
        if (durationMinutes >= MINUTES_FOUR_HOURS) {
            return new PackageTier(MINUTES_FOUR_HOURS, PRICE_FOUR_HOURS);
        }
        if (durationMinutes >= MINUTES_ONE_HOUR) {
            return new PackageTier(MINUTES_ONE_HOUR, PRICE_ONE_HOUR);
        }

        return null;
    }

    private record PackageTier(int durationMinutes, BigDecimal price) {}
}
