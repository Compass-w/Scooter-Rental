package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.entity.IssueReport;
import com.scooterrental.backend.entity.MaintenanceLog;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.entity.StaffBooking;
import com.scooterrental.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Admin Dashboard Module", description = "Analytics, fleet management, issue workflow, and walk-in booking")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard snapshot")
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(adminService.getDashboardSnapshot());
    }

    @PostMapping("/scooters")
    @Operation(summary = "Add scooter")
    public ResponseEntity<Result<Scooter>> addScooter(@RequestBody Scooter scooter) {
        try {
            return ResponseEntity.ok(Result.success(adminService.addScooter(scooter)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(400, ex.getMessage()));
        }
    }

    @PutMapping("/scooters/{scooterId}")
    @Operation(summary = "Update scooter configuration")
    public ResponseEntity<Result<Scooter>> updateScooter(@PathVariable Long scooterId, @RequestBody Scooter scooter) {
        try {
            return ResponseEntity.ok(Result.success(adminService.updateScooter(scooterId, scooter)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(400, ex.getMessage()));
        }
    }

    @PatchMapping("/scooters/{scooterId}/status")
    @Operation(summary = "Override scooter status")
    public ResponseEntity<Result<Scooter>> overrideStatus(
            @PathVariable Long scooterId,
            @RequestBody Map<String, String> payload) {
        try {
            return ResponseEntity.ok(Result.success(adminService.overrideScooterStatus(scooterId, payload.get("status"))));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(400, ex.getMessage()));
        }
    }

    @GetMapping("/scooters/{scooterId}/maintenance-logs")
    @Operation(summary = "Get maintenance logs for a scooter")
    public Result<List<MaintenanceLog>> getMaintenanceLogs(@PathVariable Integer scooterId) {
        return Result.success(adminService.getMaintenanceLogs(scooterId));
    }

    @PostMapping("/scooters/{scooterId}/maintenance-logs")
    @Operation(summary = "Add maintenance log for a scooter")
    public ResponseEntity<Result<MaintenanceLog>> addMaintenanceLog(
            @PathVariable Integer scooterId,
            @RequestBody MaintenanceLog maintenanceLog) {
        try {
            return ResponseEntity.ok(Result.success(adminService.addMaintenanceLog(scooterId, maintenanceLog)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(400, ex.getMessage()));
        }
    }

    @PatchMapping("/issues/{issueId}")
    @Operation(summary = "Update issue priority, workflow, or assignee")
    public ResponseEntity<Result<IssueReport>> updateIssue(
            @PathVariable Integer issueId,
            @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(Result.success(adminService.updateIssue(issueId, payload)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(400, ex.getMessage()));
        }
    }

    @GetMapping("/pos/bookings")
    @Operation(summary = "List walk-in staff bookings")
    public Result<List<StaffBooking>> getStaffBookings() {
        return Result.success(adminService.getStaffBookings());
    }

    @PostMapping("/pos/bookings")
    @Operation(summary = "Create walk-in staff booking")
    public ResponseEntity<Result<StaffBooking>> createStaffBooking(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(Result.success(adminService.createStaffBooking(payload)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(400, ex.getMessage()));
        }
    }

    @PostMapping("/pos/bookings/{bookingId}/confirmation")
    @Operation(summary = "Send booking confirmation email manually")
    public ResponseEntity<Result<StaffBooking>> sendConfirmation(
            @PathVariable Integer bookingId,
            @RequestParam(required = false) String email,
            @RequestBody(required = false) Map<String, Object> payload) {
        try {
            String resolvedEmail = email;
            if ((resolvedEmail == null || resolvedEmail.isBlank()) && payload != null && payload.get("email") != null) {
                resolvedEmail = String.valueOf(payload.get("email"));
            }
            return ResponseEntity.ok(Result.success(adminService.sendConfirmation(bookingId, resolvedEmail)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(400, ex.getMessage()));
        }
    }
}
