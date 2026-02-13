package com.scooterrental.backend.controller;

import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.service.ScooterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing scooters.
 * Handles requests related to scooter availability, details, and management.
 */
@RestController
@RequestMapping("/api/scooters")
@CrossOrigin(origins = "*") // Allow Cross-Origin Resource Sharing (CORS) for frontend
@Tag(name = "Scooter Module", description = "Manage scooter status and location")
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    /**
     * Get a list of available scooters.
     * Used by the frontend Map interface.
     *
     * @param lat Optional user latitude for sorting
     * @param lng Optional user longitude for sorting
     * @return List of Scooter objects
     */
    @GetMapping("/available")
    @Operation(summary = "Get Available Scooters", description = "Returns a list of scooters with status 'AVAILABLE', optionally sorted by distance.")
    public ResponseEntity<List<Scooter>> getAvailableScooters(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {
        List<Scooter> scooters = scooterService.getAvailableScooters(lat, lng);
        return ResponseEntity.ok(scooters);
    }

    /**
     * Get details of a specific scooter by ID.
     * Typically used after scanning a QR code.
     *
     * @param scooterId The ID of the scooter
     * @return Scooter object if found, otherwise 404
     */
    @GetMapping("/{scooterId}")
    @Operation(summary = "Get Scooter Details", description = "Retrieve details by ID (e.g., after scanning QR code)")
    public ResponseEntity<Scooter> getScooterById(@PathVariable Long scooterId) {
        Scooter scooter = scooterService.getById(scooterId);
        if (scooter != null) {
            return ResponseEntity.ok(scooter);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Add a new scooter to the fleet (Admin only).
     *
     * @param scooter The scooter object to add
     * @return Response message
     */
    @PostMapping("/add")
    @Operation(summary = "Add Scooter", description = "Admin adds a new scooter to the fleet")
    public ResponseEntity<?> addScooter(@RequestBody Scooter scooter) {
        boolean saved = scooterService.save(scooter);
        if (saved) {
            return ResponseEntity.ok(Map.of("message", "Scooter added successfully", "id", scooter.getScooterId()));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to add scooter"));
        }
    }
}
