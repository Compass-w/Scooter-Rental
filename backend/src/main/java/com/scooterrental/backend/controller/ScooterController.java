package com.scooterrental.backend.controller;

import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.service.ScooterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scooters")
@CrossOrigin(origins = "*") // Allow Cross-Origin Resource Sharing (CORS) for frontend
@Tag(name = "Scooter Module", description = "Manage scooter status and location")
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    // 1. Get all available scooters (Used for map rendering)
    @GetMapping("/available")
    @Operation(summary = "Get Available Scooters", description = "Returns only available scooters from the cloud database")
    public Map<String, Object> getAvailableScooters(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {

        // Retrieve real data from PostgreSQL cloud database
        List<Scooter> scooters = scooterService.getAvailableScooters(lat, lng);

        // Wrap the response in a standardized JSON format for the frontend
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", scooters);
        response.put("msg", "Success");
        return response;
    }

    // 2. Get details of a single scooter by ID (Used when scanning a QR code)
    @GetMapping("/{scooterId}")
    @Operation(summary = "Get Scooter Details", description = "Retrieve scooter details by its ID")
    public Map<String, Object> getScooterById(@PathVariable Long scooterId) {
        Scooter scooter = scooterService.getById(scooterId);

        Map<String, Object> response = new HashMap<>();
        if (scooter != null) {
            response.put("code", 200);
            response.put("data", scooter);
            response.put("msg", "Success");
        } else {
            response.put("code", 404);
            response.put("data", null);
            response.put("msg", "Scooter not found");
        }
        return response;
    }

    // 3. Add a new scooter (Admin functionality)
    @PostMapping("/add")
    @Operation(summary = "Add Scooter", description = "Admin adds a new scooter to the system")
    public Map<String, Object> addScooter(@RequestBody Scooter scooter) {
        boolean saved = scooterService.save(scooter);

        Map<String, Object> response = new HashMap<>();
        if (saved) {
            response.put("code", 200);
            // Return the newly generated ID to the frontend
            response.put("data", Map.of("id", scooter.getScooterId()));
            response.put("msg", "Scooter added successfully");
        } else {
            response.put("code", 400);
            response.put("data", null);
            response.put("msg", "Failed to add scooter");
        }
        return response;
    }
}
