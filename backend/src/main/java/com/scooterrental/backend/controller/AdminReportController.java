package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.dto.admin.IncomeReportResponse;
import com.scooterrental.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Admin Reports", description = "Dashboard income analytics for admin views")
public class AdminReportController {

    private final BookingService bookingService;

    public AdminReportController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/api/admin/reports/income")
    @Operation(summary = "Get income report")
    public ResponseEntity<Result<IncomeReportResponse>> getIncomeReport(
            @RequestParam(defaultValue = "daily") String type
    ) {
        try {
            return ResponseEntity.ok(Result.success(bookingService.getIncomeReport(type)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, ex.getMessage()));
        }
    }
}
