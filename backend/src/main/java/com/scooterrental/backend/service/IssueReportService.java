package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.mapper.IssueReportMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class IssueReportService {

    private static final String STATUS_REPORTED = "REPORTED";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_FIXED = "FIXED";
    private static final String PRIORITY_HIGH = "HIGH";
    private static final String PRIORITY_MEDIUM = "MEDIUM";
    private static final String PRIORITY_LOW = "LOW";

    private final IssueReportMapper issueReportMapper;

    public IssueReportService(IssueReportMapper issueReportMapper) {
        this.issueReportMapper = issueReportMapper;
    }

    public IssueReport createIssueReport(IssueReport issueReport) {
        ensureSchema();

        if (issueReport == null || issueReport.getScooterId() == null) {
            throw new IllegalArgumentException("Scooter ID is required");
        }

        String description = String.valueOf(issueReport.getDescription() == null ? "" : issueReport.getDescription()).trim();
        if (description.length() < 8) {
            throw new IllegalArgumentException("Please provide at least 8 characters of detail");
        }

        issueReport.setDescription(description);
        issueReport.setCategory(normalizeCategory(issueReport.getCategory()));
        issueReport.setPriority(resolvePriority(issueReport));
        issueReport.setStatus(STATUS_REPORTED);
        issueReportMapper.insert(issueReport);

        return issueReport;
    }

    public List<IssueReport> getIssueReports(String priority) {
        ensureSchema();
        return issueReportMapper.selectByPriority(normalizePriority(priority));
    }

    public void updateIssueStatus(Integer issueId, String status) {
        ensureSchema();

        if (issueId == null) {
            throw new IllegalArgumentException("Issue ID is required");
        }

        String normalizedStatus = normalizeStatus(status);
        if (issueReportMapper.updateStatus(issueId, normalizedStatus) == 0) {
            throw new IllegalArgumentException("Issue not found");
        }
    }

    private String normalizeCategory(String category) {
        String normalized = String.valueOf(category == null ? "OTHER" : category).trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "MECHANICAL", "ELECTRICAL", "BATTERY", "OTHER" -> normalized;
            default -> "OTHER";
        };
    }

    private String normalizePriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return null;
        }

        return switch (priority.trim().toUpperCase(Locale.ROOT)) {
            case PRIORITY_HIGH -> PRIORITY_HIGH;
            case PRIORITY_MEDIUM -> PRIORITY_MEDIUM;
            case PRIORITY_LOW -> PRIORITY_LOW;
            default -> throw new IllegalArgumentException("Unsupported priority: " + priority);
        };
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Workflow status is required");
        }

        return switch (status.trim().toUpperCase(Locale.ROOT)) {
            case STATUS_REPORTED -> STATUS_REPORTED;
            case STATUS_IN_PROGRESS, "IN PROGRESS" -> STATUS_IN_PROGRESS;
            case STATUS_FIXED -> STATUS_FIXED;
            default -> throw new IllegalArgumentException("Unsupported workflow status: " + status);
        };
    }

    private String resolvePriority(IssueReport issueReport) {
        String explicitPriority = normalizePriority(issueReport.getPriority());
        if (explicitPriority != null) {
            return explicitPriority;
        }

        String description = String.valueOf(issueReport.getDescription() == null ? "" : issueReport.getDescription())
                .toLowerCase(Locale.ROOT);

        if ("MECHANICAL".equals(issueReport.getCategory()) || "BATTERY".equals(issueReport.getCategory())) {
            return PRIORITY_HIGH;
        }

        if (description.contains("brake")
                || description.contains("smoke")
                || description.contains("crash")
                || description.contains("fire")
                || description.contains("unsafe")) {
            return PRIORITY_HIGH;
        }

        if ("ELECTRICAL".equals(issueReport.getCategory())) {
            return PRIORITY_MEDIUM;
        }

        return PRIORITY_LOW;
    }

    private void ensureSchema() {
        issueReportMapper.createTableIfNotExists();
        issueReportMapper.addPriorityColumnIfNotExists();
        issueReportMapper.addUpdatedAtColumnIfNotExists();
        issueReportMapper.migrateLegacyStatuses();
        issueReportMapper.backfillPriorityValues();
        issueReportMapper.backfillUpdatedAtValues();
    }
}
