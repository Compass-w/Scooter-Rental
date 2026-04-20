package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.mapper.IssueReportMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class IssueReportService {

    private final IssueReportMapper issueReportMapper;

    public IssueReportService(IssueReportMapper issueReportMapper) {
        this.issueReportMapper = issueReportMapper;
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
        issueReport.setUpdatedAt(LocalDateTime.now());
        issueReportMapper.insert(issueReport);

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
        issueReportMapper.ensurePriorityColumn();
        issueReportMapper.ensureWorkflowStatusColumn();
        issueReportMapper.ensureAssignedStaffColumn();
        issueReportMapper.ensureUpdatedAtColumn();
    }

    private String normalizeCategory(String category) {
        String normalized = String.valueOf(category == null ? "OTHER" : category).trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "MECHANICAL", "ELECTRICAL", "BATTERY", "OTHER" -> normalized;
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
                || normalizedDescription.contains("fire")
                || normalizedDescription.contains("won't unlock")
                || normalizedDescription.contains("cant unlock")
                || normalizedDescription.contains("cannot unlock")) {
            return "HIGH";
        }

        return switch (normalizedCategory) {
            case "BATTERY", "ELECTRICAL" -> "HIGH";
            case "MECHANICAL" -> "MEDIUM";
            default -> "LOW";
        };
    }

    private String suggestAssignee(String category, Integer scooterId) {
        String normalizedCategory = normalizeCategory(category);
        int selector = Math.abs(scooterId == null ? 0 : scooterId) % 3;

        return switch (normalizedCategory) {
            case "BATTERY" -> new String[] { "Battery Crew A", "Battery Crew B", "Charging Van 3" }[selector];
            case "ELECTRICAL" -> new String[] { "Electrics Desk", "Sensor Squad", "Firmware Bench" }[selector];
            case "MECHANICAL" -> new String[] { "Mechanical Bay", "Brake Team", "Field Repair Unit" }[selector];
            default -> new String[] { "Ops Triage", "City Patrol", "Support Bench" }[selector];
        };
    }
}
