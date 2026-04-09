package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.mapper.IssueReportMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class IssueReportService {

    private final IssueReportMapper issueReportMapper;

    public IssueReportService(IssueReportMapper issueReportMapper) {
        this.issueReportMapper = issueReportMapper;
    }

    public IssueReport createIssueReport(IssueReport issueReport) {
        issueReportMapper.createTableIfNotExists();

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
        issueReportMapper.insert(issueReport);

        return issueReport;
    }

    public List<IssueReport> getIssueReports() {
        issueReportMapper.createTableIfNotExists();
        return issueReportMapper.selectAll();
    }

    private String normalizeCategory(String category) {
        String normalized = String.valueOf(category == null ? "OTHER" : category).trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "MECHANICAL", "ELECTRICAL", "BATTERY", "OTHER" -> normalized;
            default -> "OTHER";
        };
    }
}
