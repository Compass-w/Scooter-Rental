package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.entity.MaintenanceLog;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.entity.StaffBooking;
import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.mapper.BookingMapper;
import com.scooterrental.backend.mapper.MaintenanceLogMapper;
import com.scooterrental.backend.mapper.StaffBookingMapper;
import com.scooterrental.backend.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final BookingMapper bookingMapper;
    private final ScooterService scooterService;
    private final IssueReportService issueReportService;
    private final MaintenanceLogMapper maintenanceLogMapper;
    private final StaffBookingMapper staffBookingMapper;
    private final UserMapper userMapper;

    public AdminService(
            BookingMapper bookingMapper,
            ScooterService scooterService,
            IssueReportService issueReportService,
            MaintenanceLogMapper maintenanceLogMapper,
            StaffBookingMapper staffBookingMapper,
            UserMapper userMapper) {
        this.bookingMapper = bookingMapper;
        this.scooterService = scooterService;
        this.issueReportService = issueReportService;
        this.maintenanceLogMapper = maintenanceLogMapper;
        this.staffBookingMapper = staffBookingMapper;
        this.userMapper = userMapper;
    }

    public Map<String, Object> getDashboardSnapshot() {
        ensureAdminTables();

        List<Booking> bookings = bookingMapper.selectAllForAdmin();
        List<Scooter> scooters = scooterService.list();
        List<IssueReport> issues = issueReportService.getIssueReports();
        List<MaintenanceLog> maintenanceLogs = maintenanceLogMapper.selectRecent();
        List<StaffBooking> staffBookings = staffBookingMapper.selectAll();
        List<User> users = List.of();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("generatedAt", LocalDateTime.now());
        response.put("analytics", buildAnalytics(bookings));
        response.put("fleet", buildFleetSnapshot(scooters, bookings, maintenanceLogs));
        response.put("issues", buildIssueSnapshot(issues));
        response.put("pos", buildPosSnapshot(staffBookings));

        try {
            users = defaultList(userMapper.selectAll());
            response.put("users", buildUserSnapshot(users, bookings));
        } catch (Exception ex) {
            log.error("Failed to build admin user snapshot", ex);
            response.put("users", buildEmptyUserSnapshot());
        }

        try {
            response.put("discountRules", buildDiscountRules(bookings));
        } catch (Exception ex) {
            log.error("Failed to build admin discount rules", ex);
            response.put("discountRules", buildEmptyDiscountRules());
        }

        try {
            response.put("releaseAudit", buildReleaseAudit(users, scooters, issues, staffBookings));
        } catch (Exception ex) {
            log.error("Failed to build admin release audit", ex);
            response.put("releaseAudit", buildEmptyReleaseAudit());
        }

        try {
            response.put("recommendations", buildRecommendations(bookings, scooters, issues, staffBookings, users));
        } catch (Exception ex) {
            log.error("Failed to build admin recommendations", ex);
            response.put("recommendations", List.of(mapOf(
                    "title", "Dashboard fallback mode",
                    "detail", "Some optional admin insights could not be generated, but core fleet and issue data is still available.",
                    "level", "warning")));
        }
        return response;
    }

    public Scooter addScooter(Scooter scooter) {
        Scooter sanitized = sanitizeScooter(scooter, null);
        scooterService.save(sanitized);
        return sanitized;
    }

    public Scooter updateScooter(Long scooterId, Scooter payload) {
        Scooter existing = scooterService.getById(scooterId);
        if (existing == null) {
            throw new IllegalArgumentException("Scooter not found");
        }

        Scooter sanitized = sanitizeScooter(payload, existing);
        sanitized.setScooterId(scooterId);
        scooterService.updateById(sanitized);
        return scooterService.getById(scooterId);
    }

    public Scooter overrideScooterStatus(Long scooterId, String status) {
        Scooter scooter = scooterService.getById(scooterId);
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }

        scooter.setStatus(normalizeScooterStatus(status));
        scooterService.updateById(scooter);
        return scooterService.getById(scooterId);
    }

    public List<MaintenanceLog> getMaintenanceLogs(Integer scooterId) {
        ensureAdminTables();
        return maintenanceLogMapper.selectByScooterId(scooterId);
    }

    public MaintenanceLog addMaintenanceLog(Integer scooterId, MaintenanceLog input) {
        ensureAdminTables();

        Scooter scooter = scooterService.getById(Long.valueOf(scooterId));
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }

        MaintenanceLog log = new MaintenanceLog();
        log.setScooterId(scooterId);
        log.setTechnicianName(trimToDefault(input == null ? null : input.getTechnicianName(), "Ops Workshop"));
        log.setActionTaken(trimToDefault(input == null ? null : input.getActionTaken(), "Inspection"));
        log.setNotes(input == null ? null : trimToNull(input.getNotes()));
        log.setBatteryLevel(resolveBatteryLevel(input == null ? null : input.getBatteryLevel(), scooter.getBatteryLevel()));
        maintenanceLogMapper.insert(log);

        scooter.setBatteryLevel(log.getBatteryLevel());
        if (log.getBatteryLevel() != null && log.getBatteryLevel() < 20) {
            scooter.setStatus("LOW_BATTERY");
        } else if (log.getActionTaken().toLowerCase(Locale.ROOT).contains("repair")
                || log.getActionTaken().toLowerCase(Locale.ROOT).contains("diagnostic")) {
            scooter.setStatus("MAINTENANCE");
        } else {
            scooter.setStatus("AVAILABLE");
        }
        scooterService.updateById(scooter);

        return maintenanceLogMapper.selectByScooterId(scooterId).stream().findFirst().orElse(log);
    }

    public IssueReport updateIssue(Integer issueId, Map<String, Object> payload) {
        String priority = payload == null ? null : Objects.toString(payload.get("priority"), null);
        String workflowStatus = payload == null ? null : Objects.toString(payload.get("workflowStatus"), null);
        String assignedStaff = payload == null ? null : Objects.toString(payload.get("assignedStaff"), null);
        boolean autoAssign = payload != null && Boolean.TRUE.equals(payload.get("autoAssign"));
        return issueReportService.updateIssue(issueId, priority, workflowStatus, assignedStaff, autoAssign);
    }

    public StaffBooking createStaffBooking(Map<String, Object> payload) {
        ensureAdminTables();

        Integer scooterId = parseInteger(payload == null ? null : payload.get("scooterId"));
        if (scooterId == null) {
            throw new IllegalArgumentException("Scooter ID is required");
        }

        Scooter scooter = scooterService.getById(Long.valueOf(scooterId));
        if (scooter == null) {
            throw new IllegalArgumentException("Scooter not found");
        }

        String guestName = trimToDefault(payload == null ? null : payload.get("guestName"), null);
        if (guestName == null) {
            throw new IllegalArgumentException("Guest name is required");
        }

        String hirePeriod = normalizeHirePeriod(Objects.toString(payload == null ? null : payload.get("hirePeriod"), "1_HOUR"));
        StaffBooking booking = new StaffBooking();
        booking.setGuestName(guestName);
        booking.setGuestEmail(trimToNull(Objects.toString(payload == null ? null : payload.get("guestEmail"), null)));
        booking.setScooterId(scooterId);
        booking.setHirePeriod(hirePeriod);
        booking.setDesiredStartTime(parseDateTime(payload == null ? null : payload.get("desiredStartTime")));
        booking.setEstimatedCost(estimateHireCost(hirePeriod, scooter));
        booking.setBookingStatus(Boolean.TRUE.equals(payload == null ? null : payload.get("sendConfirmation"))
                && booking.getGuestEmail() != null ? "CONFIRMATION_SENT" : "BOOKED");
        booking.setConfirmationSentAt("CONFIRMATION_SENT".equals(booking.getBookingStatus()) ? LocalDateTime.now() : null);
        booking.setNotes(trimToNull(Objects.toString(payload == null ? null : payload.get("notes"), null)));
        booking.setCreatedBy(trimToDefault(payload == null ? null : payload.get("createdBy"), "Front Desk"));
        staffBookingMapper.insert(booking);
        return staffBookingMapper.selectById(booking.getBookingId());
    }

    public StaffBooking sendConfirmation(Integer bookingId, String email) {
        ensureAdminTables();

        StaffBooking existing = staffBookingMapper.selectById(bookingId);
        if (existing == null) {
            throw new IllegalArgumentException("Staff booking not found");
        }

        String resolvedEmail = trimToNull(email);
        if (resolvedEmail == null) {
            resolvedEmail = trimToNull(existing.getGuestEmail());
        }
        if (resolvedEmail == null) {
            throw new IllegalArgumentException("Guest email is required to send confirmation");
        }

        staffBookingMapper.updateConfirmationStatus(bookingId, resolvedEmail, LocalDateTime.now(), "CONFIRMATION_SENT");
        return staffBookingMapper.selectById(bookingId);
    }

    public List<StaffBooking> getStaffBookings() {
        ensureAdminTables();
        return staffBookingMapper.selectAll();
    }

    private void ensureAdminTables() {
        maintenanceLogMapper.createTableIfNotExists();
        staffBookingMapper.createTableIfNotExists();
    }

    private Map<String, Object> buildAnalytics(List<Booking> bookings) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.minusDays(29);

        List<Booking> completed = bookings.stream()
                .filter(item -> "COMPLETED".equalsIgnoreCase(item.getStatus()))
                .toList();

        List<Booking> weeklyCompleted = completed.stream()
                .filter(item -> !resolveBookingDate(item).isBefore(weekStart))
                .toList();

        List<Booking> monthlyCompleted = completed.stream()
                .filter(item -> !resolveBookingDate(item).isBefore(monthStart))
                .toList();

        Map<String, BigDecimal> weeklyRevenueByPeriod = new LinkedHashMap<>();
        Map<String, Integer> weeklyBookingsByPeriod = new LinkedHashMap<>();
        seedPeriodMap(weeklyRevenueByPeriod, BigDecimal.ZERO);
        seedPeriodMap(weeklyBookingsByPeriod, 0);

        Map<String, Integer> popularCountByPeriod = new LinkedHashMap<>();
        seedPeriodMap(popularCountByPeriod, 0);

        for (Booking booking : weeklyCompleted) {
            String period = classifyHirePeriod(booking.getDurationMinutes());
            weeklyRevenueByPeriod.put(period, weeklyRevenueByPeriod.get(period).add(zeroMoney(booking.getTotalCost())));
            weeklyBookingsByPeriod.put(period, weeklyBookingsByPeriod.get(period) + 1);
        }

        for (Booking booking : monthlyCompleted) {
            String period = classifyHirePeriod(booking.getDurationMinutes());
            popularCountByPeriod.put(period, popularCountByPeriod.get(period) + 1);
        }

        List<Map<String, Object>> weeklyRevenueTable = weeklyRevenueByPeriod.entrySet().stream()
                .map(entry -> mapOf(
                        "hirePeriod", entry.getKey(),
                        "revenue", scale(entry.getValue()),
                        "bookings", weeklyBookingsByPeriod.get(entry.getKey())))
                .toList();

        List<Map<String, Object>> dailyRevenueTable = new ArrayList<>();
        BigDecimal weekTotal = BigDecimal.ZERO;
        BigDecimal todayRevenue = BigDecimal.ZERO;

        for (int index = 0; index < 7; index++) {
            LocalDate date = weekStart.plusDays(index);
            List<Booking> bookingsForDay = weeklyCompleted.stream()
                    .filter(item -> resolveBookingDate(item).isEqual(date))
                    .toList();

            BigDecimal revenue = bookingsForDay.stream()
                    .map(item -> zeroMoney(item.getTotalCost()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int bookingsCount = bookingsForDay.size();
            dailyRevenueTable.add(mapOf(
                    "date", date.toString(),
                    "label", date.format(DAY_FORMATTER),
                    "revenue", scale(revenue),
                    "bookings", bookingsCount));

            weekTotal = weekTotal.add(revenue);
            if (date.isEqual(today)) {
                todayRevenue = revenue;
            }
        }

        String topPeriod = popularCountByPeriod.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .orElse("No clear leader");

        Map<String, Object> financialSummary = new LinkedHashMap<>();
        financialSummary.put("weeklyRevenue", scale(weekTotal));
        financialSummary.put("dailyRevenue", scale(todayRevenue));
        financialSummary.put("averageOrderValue", scale(weeklyCompleted.isEmpty()
                ? BigDecimal.ZERO
                : weekTotal.divide(BigDecimal.valueOf(weeklyCompleted.size()), 2, RoundingMode.HALF_UP)));
        financialSummary.put("completedRides", weeklyCompleted.size());
        financialSummary.put("popularPeriod", topPeriod);

        return Map.of(
                "financialSummary", financialSummary,
                "weeklyRevenueTable", weeklyRevenueTable,
                "dailyRevenueTable", dailyRevenueTable,
                "popularPeriods", popularCountByPeriod.entrySet().stream()
                        .map(entry -> mapOf("hirePeriod", entry.getKey(), "count", entry.getValue()))
                        .sorted((left, right) -> Integer.compare((Integer) right.get("count"), (Integer) left.get("count")))
                        .toList(),
                "chart", mapOf(
                        "dailyLabels", dailyRevenueTable.stream().map(item -> item.get("label")).toList(),
                        "dailyRevenue", dailyRevenueTable.stream().map(item -> item.get("revenue")).toList(),
                        "weeklyLabels", weeklyRevenueTable.stream().map(item -> item.get("hirePeriod")).toList(),
                        "weeklyRevenue", weeklyRevenueTable.stream().map(item -> item.get("revenue")).toList()));
    }

    private Map<String, Object> buildFleetSnapshot(
            List<Scooter> scooters,
            List<Booking> bookings,
            List<MaintenanceLog> maintenanceLogs) {
        Map<String, Long> statusSummary = scooters.stream()
                .collect(Collectors.groupingBy(item -> normalizeScooterStatus(item.getStatus()), LinkedHashMap::new, Collectors.counting()));

        List<Map<String, Object>> chargingQueue = scooters.stream()
                .filter(item -> item.getBatteryLevel() != null && item.getBatteryLevel() < 20)
                .sorted(Comparator.comparing(Scooter::getBatteryLevel))
                .map(item -> mapOf(
                        "scooterId", item.getScooterId(),
                        "model", item.getModel(),
                        "batteryLevel", item.getBatteryLevel(),
                        "status", normalizeScooterStatus(item.getStatus())))
                .toList();

        Map<Integer, Long> bookingCountByScooter = bookings.stream()
                .filter(item -> item.getScooterId() != null)
                .collect(Collectors.groupingBy(Booking::getScooterId, Collectors.counting()));

        double minLat = scooters.stream().mapToDouble(item -> item.getLatitude() == null ? 0 : item.getLatitude()).min().orElse(0);
        double maxLat = scooters.stream().mapToDouble(item -> item.getLatitude() == null ? 0 : item.getLatitude()).max().orElse(1);
        double minLng = scooters.stream().mapToDouble(item -> item.getLongitude() == null ? 0 : item.getLongitude()).min().orElse(0);
        double maxLng = scooters.stream().mapToDouble(item -> item.getLongitude() == null ? 0 : item.getLongitude()).max().orElse(1);

        List<Map<String, Object>> hotspots = scooters.stream()
                .map(item -> {
                    long bookingCount = bookingCountByScooter.getOrDefault(item.getScooterId().intValue(), 0L);
                    int intensity = (int) Math.max(12, Math.min(96, 18 + bookingCount * 12 + (item.getStatus() != null && item.getStatus().equalsIgnoreCase("IN_USE") ? 18 : 0)));
                    return mapOf(
                            "scooterId", item.getScooterId(),
                            "model", item.getModel(),
                            "city", inferCity(item.getLatitude(), item.getLongitude()),
                            "latitude", item.getLatitude() == null ? 0D : item.getLatitude(),
                            "longitude", item.getLongitude() == null ? 0D : item.getLongitude(),
                            "intensity", intensity,
                            "bookings", bookingCount,
                            "x", normalizePoint(item.getLongitude(), minLng, maxLng),
                            "y", 100D - normalizePoint(item.getLatitude(), minLat, maxLat));
                })
                .sorted((left, right) -> Integer.compare((Integer) right.get("intensity"), (Integer) left.get("intensity")))
                .limit(18)
                .toList();

        List<Map<String, Object>> scootersView = scooters.stream()
                .sorted(Comparator.comparing(item -> item.getScooterId() == null ? 0L : item.getScooterId()))
                .map(item -> mapOf(
                        "id", item.getScooterId(),
                        "model", item.getModel(),
                        "batteryLevel", item.getBatteryLevel() == null ? 0 : item.getBatteryLevel(),
                        "status", normalizeScooterStatus(item.getStatus()),
                        "basePrice", scale(item.getBasePrice()),
                        "pricePerMin", scale(item.getPricePerMin()),
                        "latitude", item.getLatitude() == null ? 0D : item.getLatitude(),
                        "longitude", item.getLongitude() == null ? 0D : item.getLongitude(),
                        "city", inferCity(item.getLatitude(), item.getLongitude())))
                .toList();

        List<Map<String, Object>> recentLogs = maintenanceLogs.stream()
                .limit(12)
                .map(item -> mapOf(
                        "logId", item.getLogId(),
                        "scooterId", item.getScooterId(),
                        "technicianName", item.getTechnicianName(),
                        "actionTaken", item.getActionTaken(),
                        "notes", item.getNotes() == null ? "" : item.getNotes(),
                        "batteryLevel", item.getBatteryLevel() == null ? 0 : item.getBatteryLevel(),
                        "createdAt", item.getCreatedAt()))
                .toList();

        return Map.of(
                "statusSummary", statusSummary,
                "chargingQueue", chargingQueue,
                "hotspots", hotspots,
                "scooters", scootersView,
                "maintenanceLogs", recentLogs);
    }

    private Map<String, Object> buildIssueSnapshot(List<IssueReport> issues) {
        List<Map<String, Object>> issueRows = issues.stream()
                .map(item -> mapOf(
                        "issueId", item.getIssueId(),
                        "scooterId", item.getScooterId(),
                        "category", item.getCategory() == null ? "OTHER" : item.getCategory(),
                        "description", item.getDescription() == null ? "" : item.getDescription(),
                        "status", item.getStatus() == null ? "OPEN" : item.getStatus(),
                        "priority", item.getPriority() == null ? "MEDIUM" : item.getPriority(),
                        "workflowStatus", item.getWorkflowStatus() == null ? "REPORTED" : item.getWorkflowStatus(),
                        "assignedStaff", item.getAssignedStaff() == null ? "Unassigned" : item.getAssignedStaff(),
                        "createdAt", item.getCreatedAt(),
                        "updatedAt", item.getUpdatedAt()))
                .toList();

        long highPriorityCount = issueRows.stream()
                .filter(item -> "HIGH".equals(item.get("priority")) || "CRITICAL".equals(item.get("priority")))
                .count();

        Map<String, Long> workflowSummary = issueRows.stream()
                .collect(Collectors.groupingBy(item -> String.valueOf(item.get("workflowStatus")), LinkedHashMap::new, Collectors.counting()));

        return Map.of(
                "records", issueRows,
                "summary", Map.of(
                        "total", issueRows.size(),
                        "highPriority", highPriorityCount,
                        "workflow", workflowSummary),
                "highPriorityView", issueRows.stream()
                        .filter(item -> "HIGH".equals(item.get("priority")) || "CRITICAL".equals(item.get("priority")))
                        .toList());
    }

    private Map<String, Object> buildPosSnapshot(List<StaffBooking> staffBookings) {
        List<Map<String, Object>> recentBookings = staffBookings.stream()
                .limit(10)
                .map(item -> mapOf(
                        "bookingId", item.getBookingId(),
                        "guestName", item.getGuestName(),
                        "guestEmail", item.getGuestEmail() == null ? "" : item.getGuestEmail(),
                        "scooterId", item.getScooterId(),
                        "scooterModel", item.getScooterModel() == null ? "Scooter" : item.getScooterModel(),
                        "hirePeriod", item.getHirePeriod(),
                        "estimatedCost", scale(item.getEstimatedCost()),
                        "bookingStatus", item.getBookingStatus(),
                        "confirmationSentAt", item.getConfirmationSentAt(),
                        "desiredStartTime", item.getDesiredStartTime(),
                        "createdBy", item.getCreatedBy()))
                .toList();

        return Map.of(
                "recentBookings", recentBookings,
                "summary", Map.of(
                        "count", staffBookings.size(),
                        "sentConfirmations", staffBookings.stream()
                                .filter(item -> item.getConfirmationSentAt() != null)
                                .count()));
    }

    private Map<String, Object> buildUserSnapshot(List<User> users, List<Booking> bookings) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        Map<Integer, List<Booking>> bookingsByUser = bookings.stream()
                .filter(item -> item.getUserId() != null)
                .collect(Collectors.groupingBy(Booking::getUserId));

        Map<Integer, Booking> activeBookingByUser = bookings.stream()
                .filter(item -> item.getUserId() != null)
                .filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus()))
                .collect(Collectors.toMap(
                        Booking::getUserId,
                        item -> item,
                        (left, right) -> compareDateTime(left.getStartTime(), right.getStartTime()) >= 0 ? left : right,
                        LinkedHashMap::new));

        List<Map<String, Object>> records = users.stream()
                .sorted((left, right) -> compareDateTime(right.getCreatedAt(), left.getCreatedAt()))
                .map(user -> {
                    List<Booking> userBookings = bookingsByUser.getOrDefault(user.getUserId(), List.of());
                    Booking activeBooking = activeBookingByUser.get(user.getUserId());

                    long completedBookings = userBookings.stream()
                            .filter(item -> "COMPLETED".equalsIgnoreCase(item.getStatus()))
                            .count();

                    BigDecimal lifetimeSpend = userBookings.stream()
                            .filter(item -> "COMPLETED".equalsIgnoreCase(item.getStatus()))
                            .map(item -> zeroMoney(item.getTotalCost()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    LocalDateTime lastRideAt = userBookings.stream()
                            .map(this::resolveBookingDateTime)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    String achievements = trimToNull(user.getAchievements());
                    return mapOf(
                            "userId", user.getUserId(),
                            "username", defaultText(user.getUsername(), "Guest"),
                            "email", defaultText(user.getEmail(), "No email"),
                            "phone", defaultText(user.getPhone(), "No phone"),
                            "city", defaultText(user.getCity(), "Unknown city"),
                            "role", defaultText(user.getRole(), "customer").toUpperCase(Locale.ROOT),
                            "avatarUrl", defaultText(user.getAvatarUrl(), ""),
                            "achievements", achievements == null
                                    ? List.of()
                                    : Arrays.stream(achievements.split("\\s*,\\s*"))
                                            .map(String::trim)
                                            .filter(item -> !item.isEmpty())
                                            .toList(),
                            "totalRidingMinutes", user.getTotalRidingMinutes() == null ? 0 : user.getTotalRidingMinutes(),
                            "totalBookings", userBookings.size(),
                            "completedBookings", completedBookings,
                            "lifetimeSpend", scale(lifetimeSpend),
                            "activeRide", activeBooking != null,
                            "activeBookingId", activeBooking == null ? null : activeBooking.getBookingId(),
                            "activeScooterId", activeBooking == null ? null : activeBooking.getScooterId(),
                            "lastRideAt", lastRideAt,
                            "createdAt", user.getCreatedAt());
                })
                .toList();

        Map<String, Long> citySummary = users.stream()
                .map(user -> defaultText(trimToNull(user.getCity()), "Unknown city"))
                .collect(Collectors.groupingBy(city -> city, LinkedHashMap::new, Collectors.counting()));

        String topCity = citySummary.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown city");

        long loyaltyMembers = users.stream()
                .filter(user -> trimToNull(user.getAchievements()) != null)
                .count();

        return mapOf(
                "records", records,
                "summary", mapOf(
                        "totalUsers", users.size(),
                        "activeRiders", activeBookingByUser.size(),
                        "newThisWeek", users.stream().filter(user -> user.getCreatedAt() != null && !user.getCreatedAt().isBefore(weekAgo)).count(),
                        "loyaltyMembers", loyaltyMembers,
                        "syncedCities", citySummary.size(),
                        "topCity", topCity));
    }

    private Map<String, Object> buildDiscountRules(List<Booking> bookings) {
        List<Map<String, Object>> rules = List.of(
                buildDiscountRule("1_HOUR", "1 hour", BigDecimal.valueOf(3.50), BigDecimal.valueOf(3.50), "Quick errands and short campus hops", bookings),
                buildDiscountRule("4_HOURS", "4 hours", BigDecimal.valueOf(12.00), BigDecimal.valueOf(14.00), "Half-day trips, repairs, and reserve-and-return errands", bookings),
                buildDiscountRule("1_DAY", "1 day", BigDecimal.valueOf(30.00), BigDecimal.valueOf(84.00), "Day rentals and tourism-heavy demand", bookings),
                buildDiscountRule("1_WEEK", "1 week", BigDecimal.valueOf(100.00), BigDecimal.valueOf(210.00), "Longer-term commuting, guest riders, and promotions", bookings));

        String mostUsedRule = rules.stream()
                .max(Comparator.comparingInt(item -> (Integer) item.get("usageCount")))
                .map(item -> String.valueOf(item.get("label")))
                .orElse("1 hour");

        double avgSavingsRate = rules.stream()
                .mapToDouble(item -> ((BigDecimal) item.get("savingsRate")).doubleValue())
                .average()
                .orElse(0);

        return mapOf(
                "rules", rules,
                "summary", mapOf(
                        "activeRules", rules.size(),
                        "mostUsedRule", mostUsedRule,
                        "averageSavingsRate", BigDecimal.valueOf(avgSavingsRate).setScale(2, RoundingMode.HALF_UP)));
    }

    private Map<String, Object> buildReleaseAudit(
            List<User> users,
            List<Scooter> scooters,
            List<IssueReport> issues,
            List<StaffBooking> staffBookings) {
        List<Map<String, Object>> items = List.of(
                mapOf(
                        "title", "Analytics dashboard",
                        "status", "READY",
                        "detail", "Revenue tables, charting, popularity tracking, and export flows are connected."),
                mapOf(
                        "title", "Fleet controls",
                        "status", scooters.isEmpty() ? "WATCH" : "READY",
                        "detail", scooters.isEmpty()
                                ? "Fleet data is empty, so configuration and heatmap views need seeded scooters."
                                : "Scooter configuration, remote status override, charging queue, and maintenance history are available."),
                mapOf(
                        "title", "Issue workflow",
                        "status", "READY",
                        "detail", "Priority sorting, high-priority filtering, workflow transitions, and auto-assignment are in place."),
                mapOf(
                        "title", "Staff POS mode",
                        "status", "READY",
                        "detail", "Walk-in bookings and manual confirmation actions are available from the admin board."),
                mapOf(
                        "title", "User sync",
                        "status", users.isEmpty() ? "WATCH" : "READY",
                        "detail", users.isEmpty()
                                ? "No user rows were found, so the admin board cannot mirror customer activity yet."
                                : "Customer profiles, ride totals, loyalty signals, and active rides are synced into admin."),
                mapOf(
                        "title", "Discount rules and 4-hour package",
                        "status", "READY",
                        "detail", "Discount ladders are exposed in admin and the 4-hour package can now be surfaced consistently."),
                mapOf(
                        "title", "Operational caveats",
                        "status", "WATCH",
                        "detail", "The heatmap is an ops visualization and booking confirmations are tracked in-app; a live GIS layer and real email provider are still optional follow-up work."));

        long readyCount = items.stream().filter(item -> "READY".equals(item.get("status"))).count();
        long watchCount = items.stream().filter(item -> "WATCH".equals(item.get("status"))).count();

        return mapOf(
                "items", items,
                "summary", mapOf(
                        "ready", readyCount,
                        "watch", watchCount,
                        "openIssues", issues.stream().filter(item -> !"FIXED".equalsIgnoreCase(item.getWorkflowStatus())).count(),
                        "recentPosBookings", staffBookings.size()));
    }

    private Map<String, Object> buildDiscountRule(
            String code,
            String label,
            BigDecimal packagePrice,
            BigDecimal referencePrice,
            String recommendedFor,
            List<Booking> bookings) {
        BigDecimal savings = referencePrice.subtract(packagePrice).max(BigDecimal.ZERO);
        BigDecimal savingsRate = referencePrice.compareTo(BigDecimal.ZERO) > 0
                ? savings.divide(referencePrice, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int usageCount = (int) bookings.stream()
                .filter(item -> "COMPLETED".equalsIgnoreCase(item.getStatus()))
                .filter(item -> label.equalsIgnoreCase(classifyHirePeriod(item.getDurationMinutes())))
                .count();

        return mapOf(
                "code", code,
                "label", label,
                "status", "ACTIVE",
                "packagePrice", scale(packagePrice),
                "referencePrice", scale(referencePrice),
                "savings", scale(savings),
                "savingsRate", savingsRate,
                "usageCount", usageCount,
                "recommendedFor", recommendedFor);
    }

    private List<Map<String, Object>> buildRecommendations(
            List<Booking> bookings,
            List<Scooter> scooters,
            List<IssueReport> issues,
            List<StaffBooking> staffBookings,
            List<User> users) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        long lowBatteryCount = scooters.stream()
                .filter(item -> item.getBatteryLevel() != null && item.getBatteryLevel() < 20)
                .count();
        if (lowBatteryCount > 0) {
            recommendations.add(Map.of(
                    "title", "Dispatch charging round",
                    "detail", lowBatteryCount + " scooters are below 20% battery and should move into the charging queue.",
                    "level", "warning"));
        }

        long urgentIssues = issues.stream()
                .filter(item -> "HIGH".equalsIgnoreCase(item.getPriority()) || "CRITICAL".equalsIgnoreCase(item.getPriority()))
                .count();
        if (urgentIssues > 0) {
            recommendations.add(Map.of(
                    "title", "Escalate urgent faults",
                    "detail", urgentIssues + " issue reports are marked high priority and should be reviewed before release.",
                    "level", "critical"));
        }

        long unresolvedIssues = issues.stream()
                .filter(item -> !"FIXED".equalsIgnoreCase(item.getWorkflowStatus()))
                .count();
        recommendations.add(Map.of(
                "title", "Ops pulse",
                "detail", bookings.stream().filter(item -> "COMPLETED".equalsIgnoreCase(item.getStatus())).count()
                        + " completed rides tracked, " + unresolvedIssues + " unresolved faults, "
                        + staffBookings.size() + " walk-in bookings logged.",
                "level", "info"));

        long newUsersThisWeek = users.stream()
                .filter(user -> user.getCreatedAt() != null && !user.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7)))
                .count();
        if (newUsersThisWeek > 0) {
            recommendations.add(Map.of(
                    "title", "Customer growth",
                    "detail", newUsersThisWeek + " users joined in the last 7 days. Consider highlighting the 4-hour package for conversion lifts.",
                    "level", "info"));
        }

        return recommendations;
    }

    private List<User> defaultList(List<User> users) {
        return users == null ? List.of() : users;
    }

    private Map<String, Object> buildEmptyUserSnapshot() {
        return mapOf(
                "records", List.of(),
                "summary", mapOf(
                        "totalUsers", 0,
                        "activeRiders", 0,
                        "newThisWeek", 0,
                        "loyaltyMembers", 0,
                        "syncedCities", 0,
                        "topCity", "Unavailable"));
    }

    private Map<String, Object> buildEmptyDiscountRules() {
        return mapOf(
                "rules", List.of(),
                "summary", mapOf(
                        "activeRules", 0,
                        "mostUsedRule", "Unavailable",
                        "averageSavingsRate", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)));
    }

    private Map<String, Object> buildEmptyReleaseAudit() {
        return mapOf(
                "items", List.of(),
                "summary", mapOf(
                        "ready", 0,
                        "watch", 0,
                        "openIssues", 0,
                        "recentPosBookings", 0));
    }

    private Map<String, Object> mapOf(Object... entries) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (entries == null) {
            return map;
        }

        for (int index = 0; index < entries.length; index += 2) {
            String key = String.valueOf(entries[index]);
            Object value = index + 1 < entries.length ? entries[index + 1] : null;
            map.put(key, value);
        }
        return map;
    }

    private Scooter sanitizeScooter(Scooter payload, Scooter existing) {
        if (payload == null && existing == null) {
            throw new IllegalArgumentException("Scooter payload is required");
        }

        Scooter scooter = existing == null ? new Scooter() : existing;
        String model = trimToDefault(payload == null ? null : payload.getModel(), scooter.getModel());
        if (model == null) {
            throw new IllegalArgumentException("Scooter model is required");
        }

        scooter.setModel(model);
        scooter.setLatitude(resolveCoordinate(payload == null ? null : payload.getLatitude(), scooter.getLatitude(), "latitude"));
        scooter.setLongitude(resolveCoordinate(payload == null ? null : payload.getLongitude(), scooter.getLongitude(), "longitude"));
        scooter.setBatteryLevel(resolveBatteryLevel(payload == null ? null : payload.getBatteryLevel(), scooter.getBatteryLevel()));
        scooter.setStatus(normalizeScooterStatus(payload == null ? null : payload.getStatus(), scooter.getStatus()));
        scooter.setBasePrice(resolveMoney(payload == null ? null : payload.getBasePrice(), scooter.getBasePrice(), "base price"));
        scooter.setPricePerMin(resolveMoney(payload == null ? null : payload.getPricePerMin(), scooter.getPricePerMin(), "price per minute"));
        return scooter;
    }

    private LocalDate resolveBookingDate(Booking booking) {
        if (booking == null) {
            return LocalDate.now();
        }
        if (booking.getEndTime() != null) {
            return booking.getEndTime().toLocalDate();
        }
        if (booking.getCreatedAt() != null) {
            return booking.getCreatedAt().toLocalDate();
        }
        if (booking.getStartTime() != null) {
            return booking.getStartTime().toLocalDate();
        }
        return LocalDate.now();
    }

    private String classifyHirePeriod(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 60) return "1 hour";
        if (durationMinutes <= 240) return "4 hours";
        if (durationMinutes <= 1440) return "1 day";
        if (durationMinutes <= 10080) return "1 week";
        return "Extended";
    }

    private <T> void seedPeriodMap(Map<String, T> target, T value) {
        target.put("1 hour", value);
        target.put("4 hours", value);
        target.put("1 day", value);
        target.put("1 week", value);
        target.put("Extended", value);
    }

    private BigDecimal estimateHireCost(String hirePeriod, Scooter scooter) {
        return switch (hirePeriod) {
            case "1_HOUR" -> BigDecimal.valueOf(3.50);
            case "4_HOURS" -> BigDecimal.valueOf(12.00);
            case "1_DAY" -> BigDecimal.valueOf(30.00);
            case "1_WEEK" -> BigDecimal.valueOf(100.00);
            default -> zeroMoney(scooter.getBasePrice()).add(zeroMoney(scooter.getPricePerMin()).multiply(BigDecimal.valueOf(60)));
        };
    }

    private String normalizeHirePeriod(String hirePeriod) {
        String normalized = String.valueOf(hirePeriod == null ? "1_HOUR" : hirePeriod).trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "1_HOUR", "4_HOURS", "1_DAY", "1_WEEK" -> normalized;
            default -> "1_HOUR";
        };
    }

    private String normalizeScooterStatus(String status) {
        return normalizeScooterStatus(status, "AVAILABLE");
    }

    private String normalizeScooterStatus(String status, String fallback) {
        String normalized = String.valueOf(status == null ? fallback : status).trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "AVAILABLE", "IN_USE", "MAINTENANCE", "LOW_BATTERY", "REMOTE_LOCKED" -> normalized;
            default -> fallback == null ? "AVAILABLE" : fallback.toUpperCase(Locale.ROOT);
        };
    }

    private LocalDateTime resolveBookingDateTime(Booking booking) {
        if (booking == null) return null;
        if (booking.getEndTime() != null) return booking.getEndTime();
        if (booking.getStartTime() != null) return booking.getStartTime();
        return booking.getCreatedAt();
    }

    private int compareDateTime(LocalDateTime left, LocalDateTime right) {
        if (left == null && right == null) return 0;
        if (left == null) return -1;
        if (right == null) return 1;
        return left.compareTo(right);
    }

    private String trimToDefault(Object value, String fallback) {
        String resolved = trimToNull(value == null ? null : String.valueOf(value));
        return resolved == null ? fallback : resolved;
    }

    private String defaultText(String value, String fallback) {
        String resolved = trimToNull(value);
        return resolved == null ? fallback : resolved;
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer resolveBatteryLevel(Integer requested, Integer fallback) {
        int battery = requested != null ? requested : (fallback != null ? fallback : 100);
        return Math.max(0, Math.min(100, battery));
    }

    private Double resolveCoordinate(Double requested, Double fallback, String label) {
        Double resolved = requested != null ? requested : fallback;
        if (resolved == null) {
            throw new IllegalArgumentException("Scooter " + label + " is required");
        }
        return resolved;
    }

    private BigDecimal resolveMoney(BigDecimal requested, BigDecimal fallback, String label) {
        BigDecimal resolved = requested != null ? requested : fallback;
        if (resolved == null) {
            throw new IllegalArgumentException("Scooter " + label + " is required");
        }
        return resolved.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal zeroMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal scale(BigDecimal value) {
        return zeroMoney(value).setScale(2, RoundingMode.HALF_UP);
    }

    private Integer parseInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.intValue();
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return LocalDateTime.of(LocalDate.now(), LocalTime.now().plusMinutes(15).withSecond(0).withNano(0));
        }
        try {
            return LocalDateTime.parse(String.valueOf(value));
        } catch (Exception ignored) {
            return LocalDateTime.of(LocalDate.now(), LocalTime.now().plusMinutes(15).withSecond(0).withNano(0));
        }
    }

    private double normalizePoint(Double value, double min, double max) {
        if (value == null) return 50D;
        if (Double.compare(max, min) == 0) return 50D;
        return Math.max(6D, Math.min(94D, ((value - min) / (max - min)) * 100D));
    }

    private String inferCity(Double latitude, Double longitude) {
        double lat = latitude == null ? 0 : latitude;
        double lng = longitude == null ? 0 : longitude;

        if (lat > 30.55 && lat < 30.9 && lng > 103.9 && lng < 104.2) return "Chengdu";
        if (lat > 39.8 && lat < 40.1 && lng > 116.2 && lng < 116.5) return "Beijing";
        if (lat > 31.1 && lat < 31.3 && lng > 121.3 && lng < 121.6) return "Shanghai";
        if (lat > 22.9 && lat < 23.2 && lng > 113.1 && lng < 113.4) return "Guangzhou";
        if (lat > 22.4 && lat < 22.7 && lng > 113.8 && lng < 114.2) return "Shenzhen";
        if (lat > 30.2 && lat < 30.35 && lng > 120.0 && lng < 120.3) return "Hangzhou";
        if (lat > 32.0 && lat < 32.15 && lng > 118.7 && lng < 118.9) return "Nanjing";
        if (lat > 29.45 && lat < 29.7 && lng > 106.45 && lng < 106.7) return "Chongqing";
        return "Network Zone";
    }
}
