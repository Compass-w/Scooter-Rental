package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.mapper.IssueReportMapper;
import com.scooterrental.backend.mapper.ScooterMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class IssueReportService {

    private final IssueReportMapper issueReportMapper;
    private final ScooterMapper scooterMapper;

    public IssueReportService(IssueReportMapper issueReportMapper, ScooterMapper scooterMapper) {
        this.issueReportMapper = issueReportMapper;
        this.scooterMapper = scooterMapper;
    }

    public IssueReport createIssueReport(IssueReport issueReport) {
        ensureAdminColumns();

        if (issueReport == null || issueReport.getScooterId() == null) {
            throw new IllegalArgumentException("Scooter ID is required");
        }

        String description = String.valueOf(issueReport.getDescription() == null ? "" : issueReport.getDescription()).trim();
        if (description.length() < 8) {
            throw new IllegalArgumentException("Please provide at least 8 characters of detail");
        }

        issueReport.setDescription(description);
        issueReport.setCategory(normalizeCategory(issueReport.getCategory()));
        issueReport.setStatus("OPEN");
        issueReport.setPriority(resolvePriority(issueReport.getCategory(), description));
        issueReport.setWorkflowStatus("REPORTED");
        issueReport.setAssignedStaff(suggestAssignee(issueReport.getCategory(), issueReport.getScooterId()));
        issueReport.setSafetyAction(resolveSafetyAction(issueReport));
        issueReport.setInsuranceCaseStatus(resolveInsuranceCaseStatus(issueReport));
        issueReport.setCustomerChargePolicy(resolveCustomerChargePolicy(issueReport));
        issueReport.setRepairChargeEstimate(resolveRepairChargeEstimate(issueReport));
        issueReport.setRiderInjured(Boolean.TRUE.equals(issueReport.getRiderInjured()));
        issueReport.setThirdPartyInvolved(Boolean.TRUE.equals(issueReport.getThirdPartyInvolved()));
        issueReport.setEmergencyServicesContacted(Boolean.TRUE.equals(issueReport.getEmergencyServicesContacted()));
        issueReport.setUpdatedAt(LocalDateTime.now());
        issueReportMapper.insert(issueReport);
        holdScooterForMaintenance(issueReport);

        return issueReport;
    }

    public List<IssueReport> getIssueReports() {
        ensureAdminColumns();
        return issueReportMapper.selectAll();
    }

    public IssueReport updateIssue(Integer issueId, String priority, String workflowStatus, String assignedStaff, boolean autoAssign) {
        ensureAdminColumns();

        IssueReport existing = issueReportMapper.selectById(issueId);
        if (existing == null) {
            throw new IllegalArgumentException("Issue not found");
        }

        existing.setPriority(normalizePriority(priority == null || priority.isBlank() ? existing.getPriority() : priority));
        existing.setWorkflowStatus(normalizeWorkflowStatus(
                workflowStatus == null || workflowStatus.isBlank() ? existing.getWorkflowStatus() : workflowStatus));

        if (autoAssign || assignedStaff == null || assignedStaff.isBlank()) {
            existing.setAssignedStaff(suggestAssignee(existing.getCategory(), existing.getScooterId()));
        } else {
            existing.setAssignedStaff(assignedStaff.trim());
        }

        existing.setStatus("FIXED".equals(existing.getWorkflowStatus()) ? "RESOLVED" : "OPEN");
        existing.setUpdatedAt(LocalDateTime.now());
        issueReportMapper.updateAdminFields(existing);
        return issueReportMapper.selectById(issueId);
    }

    private void ensureAdminColumns() {
        issueReportMapper.createTableIfNotExists();
    }

    private String normalizeCategory(String category) {
        String normalized = String.valueOf(category == null ? "OTHER" : category).trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "MECHANICAL", "ELECTRICAL", "BATTERY", "DAMAGE", "ACCIDENT", "SAFETY", "OTHER" -> normalized;
            default -> "OTHER";
        };
    }

    private String normalizePriority(String priority) {
        String normalized = String.valueOf(priority == null ? "MEDIUM" : priority).trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "LOW", "MEDIUM", "HIGH", "CRITICAL" -> normalized;
            default -> "MEDIUM";
        };
    }

    private String normalizeWorkflowStatus(String workflowStatus) {
        String normalized = String.valueOf(workflowStatus == null ? "REPORTED" : workflowStatus)
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');
        return switch (normalized) {
            case "REPORTED", "IN_PROGRESS", "FIXED" -> normalized;
            default -> "REPORTED";
        };
    }

    private String resolvePriority(String category, String description) {
        String normalizedCategory = normalizeCategory(category);
        String normalizedDescription = String.valueOf(description == null ? "" : description).toLowerCase(Locale.ROOT);

        if (normalizedDescription.contains("smoke")
                || normalizedDescription.contains("brake")
                || normalizedDescription.contains("crash")
                || normalizedDescription.contains("injury")
                || normalizedDescription.contains("injured")
                || normalizedDescription.contains("accident")
                || normalizedDescription.contains("fire")
                || normalizedDescription.contains("won't unlock")
                || normalizedDescription.contains("cant unlock")
                || normalizedDescription.contains("cannot unlock")) {
            return normalizedCategory.equals("ACCIDENT") || normalizedCategory.equals("SAFETY") ? "CRITICAL" : "HIGH";
        }

        return switch (normalizedCategory) {
            case "ACCIDENT", "SAFETY" -> "CRITICAL";
            case "DAMAGE" -> "HIGH";
            case "BATTERY", "ELECTRICAL" -> "HIGH";
            case "MECHANICAL" -> "MEDIUM";
            default -> "LOW";
        };
    }

    private String suggestAssignee(String category, Integer scooterId) {
        String normalizedCategory = normalizeCategory(category);
        int selector = Math.abs(scooterId == null ? 0 : scooterId) % 3;

        return switch (normalizedCategory) {
            case "ACCIDENT", "SAFETY" -> new String[] { "Safety Response Lead", "Insurance Desk", "City Incident Team" }[selector];
            case "DAMAGE" -> new String[] { "Damage Assessor", "Repair Bay", "Claims Review" }[selector];
            case "BATTERY" -> new String[] { "Battery Crew A", "Battery Crew B", "Charging Van 3" }[selector];
            case "ELECTRICAL" -> new String[] { "Electrics Desk", "Sensor Squad", "Firmware Bench" }[selector];
            case "MECHANICAL" -> new String[] { "Mechanical Bay", "Brake Team", "Field Repair Unit" }[selector];
            default -> new String[] { "Ops Triage", "City Patrol", "Support Bench" }[selector];
        };
    }

    private String resolveSafetyAction(IssueReport issueReport) {
        String category = normalizeCategory(issueReport.getCategory());
        if ("ACCIDENT".equals(category) || "SAFETY".equals(category)) {
            return Boolean.TRUE.equals(issueReport.getRiderInjured()) || Boolean.TRUE.equals(issueReport.getThirdPartyInvolved())
                    ? "Stop the ride, move to safety if possible, contact emergency services when needed, then wait for support review."
                    : "Stop using the scooter, park safely, and wait for support before continuing.";
        }
        if ("DAMAGE".equals(category) || "MECHANICAL".equals(category) || "ELECTRICAL".equals(category) || "BATTERY".equals(category)) {
            return "Scooter is held from release until maintenance triage is complete.";
        }
        return "Support triage will review the report and decide whether maintenance is required.";
    }

    private String resolveInsuranceCaseStatus(IssueReport issueReport) {
        String category = normalizeCategory(issueReport.getCategory());
        if (!"ACCIDENT".equals(category) && !"SAFETY".equals(category)) {
            return "NOT_REQUIRED";
        }
        return Boolean.TRUE.equals(issueReport.getRiderInjured())
                || Boolean.TRUE.equals(issueReport.getThirdPartyInvolved())
                || Boolean.TRUE.equals(issueReport.getEmergencyServicesContacted())
                ? "INSURANCE_REVIEW_REQUIRED"
                : "SAFETY_REVIEW";
    }

    private String resolveCustomerChargePolicy(IssueReport issueReport) {
        String category = normalizeCategory(issueReport.getCategory());
        return switch (category) {
            case "ACCIDENT", "SAFETY" -> "No accident charge is finalized until insurance/liability review is complete; traffic fines and uncovered negligence remain rider responsibility.";
            case "DAMAGE" -> "Repair estimate may be charged to the saved card after staff photo review and liability confirmation.";
            case "MECHANICAL", "ELECTRICAL", "BATTERY" -> "No customer repair charge unless staff confirms misuse or rider-caused damage.";
            default -> "Support review decides whether any customer charge applies.";
        };
    }

    private BigDecimal resolveRepairChargeEstimate(IssueReport issueReport) {
        String category = normalizeCategory(issueReport.getCategory());
        BigDecimal estimate = switch (category) {
            case "ACCIDENT" -> BigDecimal.valueOf(129.00);
            case "DAMAGE" -> BigDecimal.valueOf(79.00);
            case "SAFETY" -> BigDecimal.valueOf(49.00);
            case "MECHANICAL", "ELECTRICAL" -> BigDecimal.valueOf(39.00);
            case "BATTERY" -> BigDecimal.valueOf(19.00);
            default -> BigDecimal.ZERO;
        };
        return estimate.setScale(2, RoundingMode.HALF_UP);
    }

    private void holdScooterForMaintenance(IssueReport issueReport) {
        String category = normalizeCategory(issueReport.getCategory());
        if (!List.of("MECHANICAL", "ELECTRICAL", "BATTERY", "DAMAGE", "ACCIDENT", "SAFETY").contains(category)) {
            return;
        }

        Scooter scooter = scooterMapper.selectById(issueReport.getScooterId());
        if (scooter == null) {
            return;
        }
        scooter.setStatus("MAINTENANCE");
        scooterMapper.updateById(scooter);
    }
}
