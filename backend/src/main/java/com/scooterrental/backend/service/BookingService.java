package com.scooterrental.backend.service;

import com.scooterrental.backend.dto.booking.EndRideResponse;
import com.scooterrental.backend.dto.booking.StartRideRequest;
import com.scooterrental.backend.dto.booking.StartRideResponse;
import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.entity.VehicleUnlockSession;
import com.scooterrental.backend.mapper.BookingMapper;
import com.scooterrental.backend.mapper.ScooterMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
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

    private final BookingMapper bookingMapper;
    private final ScooterMapper scooterMapper;
    private final VehicleIntegrationService vehicleIntegrationService;
    private final PaymentGatewayService paymentGatewayService;
    private volatile boolean operationalTablesReady = false;

    public BookingService(
            BookingMapper bookingMapper,
            ScooterMapper scooterMapper,
            VehicleIntegrationService vehicleIntegrationService,
            PaymentGatewayService paymentGatewayService) {
        this.bookingMapper = bookingMapper;
        this.scooterMapper = scooterMapper;
        this.vehicleIntegrationService = vehicleIntegrationService;
        this.paymentGatewayService = paymentGatewayService;
    }

    public void ensureOperationalTables() {
        if (operationalTablesReady) {
            return;
        }
        synchronized (this) {
            if (operationalTablesReady) {
                return;
            }
            bookingMapper.addPlannedEndTimeColumn();
            bookingMapper.addPlanTypeColumn();
            bookingMapper.addPaymentStatusColumn();
            bookingMapper.addUnlockStatusColumn();
            bookingMapper.addUnlockReferenceColumn();
            bookingMapper.addOvertimeFeeColumn();
            bookingMapper.addOvertimeChargeTotalColumn();
            bookingMapper.addDamageChargeTotalColumn();
            bookingMapper.addElectricityChargeTotalColumn();
            bookingMapper.addMarketCodeColumn();
            bookingMapper.addServiceModeColumn();
            bookingMapper.addBookingChannelColumn();
            bookingMapper.addPickupStoreCodeColumn();
            bookingMapper.addPickupStoreNameColumn();
            bookingMapper.addReturnStoreCodeColumn();
            bookingMapper.addReturnStoreNameColumn();
            bookingMapper.addStartBatteryLevelColumn();
            bookingMapper.addEstimatedReturnBatteryColumn();
            bookingMapper.addReturnBatteryLevelColumn();
            bookingMapper.addLiabilityAcceptedColumn();
            bookingMapper.addLastReminderAtColumn();
            vehicleIntegrationService.ensureVehicleTables();
            paymentGatewayService.ensureGatewayTables();
            operationalTablesReady = true;
        }
    }

    public List<Booking> getHistory(Integer userId) {
        ensureOperationalTables();
        return bookingMapper.selectByUserId(userId);
    }

    public boolean cancel(Integer bookingId, Integer userId) {
        ensureOperationalTables();
        return bookingMapper.cancelBooking(bookingId, userId) > 0;
    }

    public boolean isBookingOwnedBy(Integer bookingId, Integer userId) {
        ensureOperationalTables();
        if (bookingId == null || userId == null) {
            return false;
        }

        Booking booking = bookingMapper.selectByBookingId(bookingId);
        return booking != null && userId.equals(booking.getUserId());
    }

    public List<Map<String, Object>> getStats(Integer userId) {
        ensureOperationalTables();
        return bookingMapper.getWeeklyStats(userId);
    }

    @Transactional
    public StartRideResponse startRide(StartRideRequest request) {
        ensureOperationalTables();
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
        BigDecimal electricityEstimate = normalizeMoney(request.getElectricityFeeEstimate());
        BigDecimal estimatedTotal = estimatedCost.add(electricityEstimate).setScale(2, RoundingMode.HALF_UP);
        LocalDateTime startTime = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setScooterId(request.getScooterId());
        booking.setStartTime(startTime);
        booking.setDurationMinutes(durationMinutes);
        booking.setPlanType(normalizedPlanType);
        booking.setPlannedEndTime(durationMinutes == null ? null : startTime.plusMinutes(durationMinutes));
        booking.setTotalCost(estimatedTotal);
        booking.setStatus(BOOKING_ACTIVE);
        booking.setPaymentStatus("PENDING");
        booking.setUnlockStatus("PENDING");
        booking.setUnlockReference(null);
        booking.setOvertimeFeePer15Minutes(resolveOvertimeFee(request.getOvertimeFeePer15Minutes(), estimatedCost));
        booking.setOvertimeChargeTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        booking.setDamageChargeTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        booking.setElectricityChargeTotal(electricityEstimate);
        booking.setMarketCode(normalizeMarketCode(request.getMarketCode()));
        booking.setServiceMode(normalizeServiceMode(request.getServiceMode()));
        booking.setBookingChannel(normalizeOptionalText(request.getBookingChannel()));
        booking.setPickupStoreCode(normalizeOptionalText(request.getPickupStoreCode()));
        booking.setPickupStoreName(normalizeOptionalText(request.getPickupStoreName()));
        booking.setReturnStoreCode(normalizeOptionalText(request.getReturnStoreCode()));
        booking.setReturnStoreName(normalizeOptionalText(request.getReturnStoreName()));
        booking.setStartBatteryLevel(resolveBatteryLevel(request.getStartBatteryLevel(), scooter.getBatteryLevel()));
        booking.setEstimatedReturnBattery(resolveBatteryLevel(request.getEstimatedReturnBattery(), booking.getStartBatteryLevel()));
        booking.setReturnBatteryLevel(null);
        booking.setLiabilityAccepted(Boolean.TRUE.equals(request.getLiabilityAccepted()));
        booking.setLastReminderAt(null);

        bookingMapper.insertBooking(booking);

        paymentGatewayService.authorizeStartRide(
                booking,
                estimatedTotal,
                "Trip deposit authorised before unlock for " + normalizedPlanType + ".");
        booking.setPaymentStatus("AUTHORIZED");

        VehicleUnlockSession unlockSession = vehicleIntegrationService.dispatchUnlockCommand(booking, scooter, request.getScanToken());
        booking.setUnlockStatus(unlockSession.getLockState());
        booking.setUnlockReference("CMD-" + unlockSession.getCommandId());
        bookingMapper.updateAutomationState(booking);

        scooter.setStatus(SCOOTER_IN_USE);
        scooterMapper.updateById(scooter);

        return new StartRideResponse(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getScooterId(),
                normalizedPlanType,
                durationMinutes,
                booking.getTotalCost(),
                booking.getStatus(),
                booking.getPaymentStatus(),
                booking.getUnlockStatus(),
                booking.getUnlockReference(),
                unlockSession.getDeviceMessage(),
                scooter.getStatus(),
                booking.getPlannedEndTime());
    }

    @Transactional
    public EndRideResponse endRide(Integer bookingId) {
        return endRide(bookingId, null);
    }

    @Transactional
    public EndRideResponse endRide(Integer bookingId, Map<String, Object> payload) {
        ensureOperationalTables();

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
        BigDecimal baseCost = calculateCost(scooter, booking.getPlanType(), billableMinutes);
        BigDecimal overtimeCharge = normalizeMoney(booking.getOvertimeChargeTotal());
        BigDecimal damageCharge = normalizeMoney(booking.getDamageChargeTotal());
        Integer returnBatteryLevel = resolveBatteryLevel(
                parseInteger(payload == null ? null : payload.get("returnBatteryLevel")),
                booking.getEstimatedReturnBattery() != null ? booking.getEstimatedReturnBattery() : scooter.getBatteryLevel());
        BigDecimal electricityCharge = calculateElectricityCharge(booking.getStartBatteryLevel(), returnBatteryLevel, booking.getElectricityChargeTotal());
        BigDecimal finalCost = baseCost.add(overtimeCharge).add(damageCharge).add(electricityCharge).setScale(2, RoundingMode.HALF_UP);

        paymentGatewayService.captureBaseCharge(
                booking,
                baseCost,
                "Base ride charge captured when the scooter was returned.");

        if (electricityCharge.compareTo(BigDecimal.ZERO) > 0) {
            paymentGatewayService.captureAdjustment(
                    booking,
                    electricityCharge,
                    "ELECTRICITY_DELTA",
                    "Electricity delta captured from pickup battery to return battery.");
        }

        booking.setEndTime(endTime);
        booking.setDurationMinutes(billableMinutes);
        booking.setTotalCost(finalCost);
        booking.setReturnBatteryLevel(returnBatteryLevel);
        booking.setElectricityChargeTotal(electricityCharge);
        booking.setStatus(BOOKING_COMPLETED);
        booking.setPaymentStatus("CAPTURED");
        booking.setUnlockStatus("LOCKED");
        bookingMapper.completeBooking(booking);

        vehicleIntegrationService.completeRideSession(bookingId);

        scooter.setBatteryLevel(returnBatteryLevel);
        scooter.setStatus(SCOOTER_AVAILABLE);
        scooterMapper.updateById(scooter);

        return new EndRideResponse(
                booking.getBookingId(),
                booking.getScooterId(),
                booking.getDurationMinutes(),
                baseCost,
                overtimeCharge,
                damageCharge,
                electricityCharge,
                booking.getTotalCost(),
                booking.getStartBatteryLevel(),
                booking.getReturnBatteryLevel(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getPaymentStatus(),
                scooter.getStatus());
    }

    @Transactional
    public Map<String, Object> extendRide(Integer bookingId, Integer extraMinutes) {
        ensureOperationalTables();

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
        BigDecimal updatedBaseCost = calculateCost(scooter, booking.getPlanType(), nextDuration);
        BigDecimal updatedCost = updatedBaseCost
                .add(normalizeMoney(booking.getOvertimeChargeTotal()))
                .add(normalizeMoney(booking.getDamageChargeTotal()))
                .add(normalizeMoney(booking.getElectricityChargeTotal()))
                .setScale(2, RoundingMode.HALF_UP);

        booking.setDurationMinutes(nextDuration);
        booking.setTotalCost(updatedCost);
        booking.setPlannedEndTime(booking.getStartTime().plusMinutes(nextDuration));
        bookingMapper.extendActiveBooking(booking);
        bookingMapper.updateAutomationState(booking);

        Map<String, Object> response = new HashMap<>();
        response.put("bookingId", booking.getBookingId());
        response.put("scooterId", booking.getScooterId());
        response.put("addedMinutes", extraMinutes);
        response.put("durationMinutes", booking.getDurationMinutes());
        response.put("totalCost", booking.getTotalCost());
        response.put("plannedEndTime", booking.getPlannedEndTime());
        response.put("bookingStatus", booking.getStatus());
        return response;
    }

    @Transactional
    public Map<String, Object> recordTelemetry(Integer bookingId, Map<String, Object> payload) {
        ensureOperationalTables();

        Booking booking = bookingMapper.selectByBookingId(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        Scooter scooter = scooterMapper.selectById(booking.getScooterId());
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }

        Double latitude = parseDouble(payload == null ? null : payload.get("latitude"));
        Double longitude = parseDouble(payload == null ? null : payload.get("longitude"));
        Integer batteryLevel = parseInteger(payload == null ? null : payload.get("batteryLevel"));
        String lockState = payload == null ? null : String.valueOf(payload.getOrDefault("lockState", "UNLOCKED"));

        if (latitude != null) {
            scooter.setLatitude(latitude);
        }
        if (longitude != null) {
            scooter.setLongitude(longitude);
        }
        if (batteryLevel != null) {
            scooter.setBatteryLevel(Math.max(0, Math.min(100, batteryLevel)));
        }
        scooterMapper.updateById(scooter);

        vehicleIntegrationService.recordTelemetry(bookingId, lockState, "Telemetry heartbeat received from the ride session.");

        Map<String, Object> response = new HashMap<>();
        response.put("bookingId", bookingId);
        response.put("scooterId", scooter.getScooterId());
        response.put("latitude", scooter.getLatitude());
        response.put("longitude", scooter.getLongitude());
        response.put("batteryLevel", scooter.getBatteryLevel());
        response.put("unlockStatus", lockState == null ? booking.getUnlockStatus() : lockState);
        response.put("telemetryStatus", "LIVE");
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
        if (request.getScanToken() == null || request.getScanToken().isBlank()) {
            throw new IllegalArgumentException("A QR scan token is required before unlock");
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

    private BigDecimal resolveOvertimeFee(BigDecimal incomingFee, BigDecimal estimatedCost) {
        BigDecimal resolved = incomingFee != null && incomingFee.compareTo(BigDecimal.ZERO) > 0
                ? incomingFee
                : estimatedCost.multiply(BigDecimal.valueOf(0.12));
        return resolved.setScale(2, RoundingMode.HALF_UP);
    }

    private Integer resolveBatteryLevel(Integer value, Integer fallback) {
        Integer resolved = value != null ? value : fallback;
        if (resolved == null) {
            return null;
        }
        return Math.max(0, Math.min(100, resolved));
    }

    private BigDecimal calculateElectricityCharge(Integer startBatteryLevel, Integer returnBatteryLevel, BigDecimal fallbackCharge) {
        if (startBatteryLevel == null || returnBatteryLevel == null) {
            return normalizeMoney(fallbackCharge);
        }
        int batteryDelta = Math.max(0, startBatteryLevel - returnBatteryLevel);
        return BigDecimal.valueOf(batteryDelta)
                .multiply(BigDecimal.valueOf(0.18))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeMarketCode(String marketCode) {
        String normalized = String.valueOf(marketCode == null ? "CN" : marketCode).trim().toUpperCase(Locale.ROOT);
        return "UK".equals(normalized) ? "UK" : "CN";
    }

    private String normalizeServiceMode(String serviceMode) {
        String normalized = String.valueOf(serviceMode == null ? "SHARING" : serviceMode).trim().toUpperCase(Locale.ROOT);
        return "WALK_IN".equals(normalized) ? "WALK_IN" : "SHARING";
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
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

    private Double parseDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private record PackageTier(int durationMinutes, BigDecimal price) {}
}
