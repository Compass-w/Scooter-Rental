package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.AutomationEvent;
import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.mapper.AutomationEventMapper;
import com.scooterrental.backend.mapper.BookingMapper;
import com.scooterrental.backend.mapper.IssueReportMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class RideAutomationService {

    private static final String EVENT_REMINDER = "REMINDER";
    private static final String EVENT_OVERTIME = "OVERTIME_CHARGE";
    private static final String EVENT_DAMAGE = "DAMAGE_CHARGE";

    private final BookingMapper bookingMapper;
    private final IssueReportMapper issueReportMapper;
    private final AutomationEventMapper automationEventMapper;
    private final PaymentGatewayService paymentGatewayService;
    private volatile boolean automationTablesReady = false;

    public RideAutomationService(
            BookingMapper bookingMapper,
            IssueReportMapper issueReportMapper,
            AutomationEventMapper automationEventMapper,
            PaymentGatewayService paymentGatewayService) {
        this.bookingMapper = bookingMapper;
        this.issueReportMapper = issueReportMapper;
        this.automationEventMapper = automationEventMapper;
        this.paymentGatewayService = paymentGatewayService;
    }

    public void ensureAutomationTables() {
        if (automationTablesReady) {
            return;
        }
        synchronized (this) {
            if (automationTablesReady) {
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
            automationEventMapper.createTableIfNotExists();
            automationTablesReady = true;
        }
    }

    public List<AutomationEvent> getRecentEvents(int limit) {
        ensureAutomationTables();
        return automationEventMapper.selectRecent(Math.max(1, limit));
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void processAutomationQueue() {
        ensureAutomationTables();
        paymentGatewayService.ensureGatewayTables();

        List<Booking> activeBookings = bookingMapper.selectActiveBookings();
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : activeBookings) {
            handleReminder(booking, now);
            handleOvertimeCharges(booking, now);
        }

        for (IssueReport issue : issueReportMapper.selectAll()) {
            handleDamageCharges(issue);
        }
    }

    private void handleReminder(Booking booking, LocalDateTime now) {
        if (booking.getPlannedEndTime() == null) return;
        if (booking.getLastReminderAt() != null) return;

        long minutesUntilEnd = Duration.between(now, booking.getPlannedEndTime()).toMinutes();
        if (minutesUntilEnd < 0 || minutesUntilEnd > 15) {
            return;
        }

        AutomationEvent event = new AutomationEvent();
        event.setBookingId(booking.getBookingId());
        event.setEventType(EVENT_REMINDER);
        event.setStatus("COMPLETED");
        event.setAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        event.setDetail("Auto reminder issued before the planned end time.");
        event.setDueAt(booking.getPlannedEndTime());
        event.setProcessedAt(now);
        automationEventMapper.insert(event);

        booking.setLastReminderAt(now);
        bookingMapper.updateAutomationState(booking);
    }

    private void handleOvertimeCharges(Booking booking, LocalDateTime now) {
        if (booking.getPlannedEndTime() == null || now.isBefore(booking.getPlannedEndTime())) {
            return;
        }

        BigDecimal overtimeFee = normalizeMoney(booking.getOvertimeFeePer15Minutes());
        if (overtimeFee.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        long overdueMinutes = Math.max(0, Duration.between(booking.getPlannedEndTime(), now).toMinutes());
        int billableWindows = (int) Math.ceil(overdueMinutes / 15.0);
        int alreadyChargedWindows = automationEventMapper.countCompletedByBookingAndType(booking.getBookingId(), EVENT_OVERTIME);
        int newWindows = billableWindows - alreadyChargedWindows;
        if (newWindows <= 0) {
            return;
        }

        BigDecimal amount = overtimeFee.multiply(BigDecimal.valueOf(newWindows)).setScale(2, RoundingMode.HALF_UP);
        paymentGatewayService.captureAdjustment(
                booking,
                amount,
                EVENT_OVERTIME,
                "Auto-captured overdue usage for " + newWindows + " overtime interval(s).");

        AutomationEvent event = new AutomationEvent();
        event.setBookingId(booking.getBookingId());
        event.setEventType(EVENT_OVERTIME);
        event.setStatus("COMPLETED");
        event.setAmount(amount);
        event.setDetail("Overtime charge captured automatically.");
        event.setDueAt(now);
        event.setProcessedAt(now);
        automationEventMapper.insert(event);

        booking.setOvertimeChargeTotal(normalizeMoney(booking.getOvertimeChargeTotal()).add(amount));
        booking.setTotalCost(normalizeMoney(booking.getTotalCost()).add(amount));
        booking.setPaymentStatus("PARTIAL_CAPTURED");
        bookingMapper.updateAutomationState(booking);
    }

    private void handleDamageCharges(IssueReport issue) {
        if (issue == null || issue.getBookingId() == null) return;
        if (!isDamageIssue(issue)) return;
        if ("FIXED".equalsIgnoreCase(issue.getWorkflowStatus())) return;
        if (automationEventMapper.countCompletedByBookingIssueAndType(issue.getBookingId(), issue.getIssueId(), EVENT_DAMAGE) > 0) {
            return;
        }

        Booking booking = bookingMapper.selectByBookingId(issue.getBookingId());
        if (booking == null) return;

        BigDecimal damageCharge = resolveDamageCharge(issue.getPriority());
        paymentGatewayService.captureAdjustment(
                booking,
                damageCharge,
                EVENT_DAMAGE,
                "Damage review triggered an automatic card charge.");

        LocalDateTime now = LocalDateTime.now();
        AutomationEvent event = new AutomationEvent();
        event.setBookingId(issue.getBookingId());
        event.setIssueId(issue.getIssueId());
        event.setEventType(EVENT_DAMAGE);
        event.setStatus("COMPLETED");
        event.setAmount(damageCharge);
        event.setDetail("Damage charge captured after issue triage.");
        event.setDueAt(now);
        event.setProcessedAt(now);
        automationEventMapper.insert(event);

        booking.setDamageChargeTotal(normalizeMoney(booking.getDamageChargeTotal()).add(damageCharge));
        booking.setTotalCost(normalizeMoney(booking.getTotalCost()).add(damageCharge));
        booking.setPaymentStatus("PARTIAL_CAPTURED");
        bookingMapper.updateAutomationState(booking);
    }

    private boolean isDamageIssue(IssueReport issue) {
        String category = String.valueOf(issue.getCategory() == null ? "" : issue.getCategory()).toLowerCase(Locale.ROOT);
        String description = String.valueOf(issue.getDescription() == null ? "" : issue.getDescription()).toLowerCase(Locale.ROOT);
        return category.contains("damage")
                || category.contains("crash")
                || category.contains("accident")
                || description.contains("damage")
                || description.contains("scratch")
                || description.contains("crash")
                || description.contains("broken");
    }

    private BigDecimal resolveDamageCharge(String priority) {
        String normalized = String.valueOf(priority == null ? "MEDIUM" : priority).trim().toUpperCase(Locale.ROOT);
        BigDecimal charge = switch (normalized) {
            case "CRITICAL" -> BigDecimal.valueOf(129.00);
            case "HIGH" -> BigDecimal.valueOf(79.00);
            case "LOW" -> BigDecimal.valueOf(19.00);
            default -> BigDecimal.valueOf(39.00);
        };
        return charge.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }
}
