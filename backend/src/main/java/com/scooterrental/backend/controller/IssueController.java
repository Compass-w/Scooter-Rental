package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.security.AuthAccess;
import com.scooterrental.backend.service.AuthSessionService;
import com.scooterrental.backend.service.IssueReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name = "Issue Report Module", description = "Create and review scooter fault reports")
public class IssueController {

    private final IssueReportService issueReportService;

    public IssueController(IssueReportService issueReportService) {
        this.issueReportService = issueReportService;
    }

    @PostMapping("/api/issues")
    @Operation(summary = "Submit issue report")
    public ResponseEntity<Result<IssueReport>> createIssueReport(
            @RequestBody IssueReport issueReport,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        try {
            if (issueReport != null && !AuthAccess.isStaff(session)) {
                issueReport.setUserId(AuthAccess.requireAuthenticatedUserId(session));
            } else if (issueReport != null && issueReport.getUserId() != null) {
                AuthAccess.requireSelfOrStaff(session, issueReport.getUserId());
            } else {
                AuthAccess.requireAuthenticatedUserId(session);
            }
            return ResponseEntity.ok(Result.success(issueReportService.createIssueReport(issueReport)));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, ex.getMessage()));
        }
    }

    @GetMapping("/api/admin/issues")
    @Operation(summary = "List issue reports")
    public Result<List<IssueReport>> getIssueReports() {
        return Result.success(issueReportService.getIssueReports());
    }
}
