package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.service.ScooterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scooters")
@Tag(name = "Scooter Module", description = "Manage scooter status and location")
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    @GetMapping
    @Operation(summary = "Get All Scooters", description = "Returns a list of all scooters from database")
    public Map<String, Object> getAllScooters() {
        List<Scooter> scooters = scooterService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", scooters);
        response.put("msg", "Success");
        return response;
    }

    @GetMapping("/available")
    @Operation(summary = "Get Available Scooters", description = "Returns only available scooters")
    public Map<String, Object> getAvailableScooters() {
        List<Scooter> scooters = scooterService.getAvailableScooters(null, null);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", scooters);
        response.put("msg", "Success");
        return response;
    }

    @GetMapping("/{scooterId}")
    @Operation(summary = "Get Scooter Detail", description = "Returns one scooter by ID")
    public ResponseEntity<Result<Scooter>> getScooterById(@PathVariable Integer scooterId) {
        Scooter scooter = scooterService.getById(scooterId);
        if (scooter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "Scooter not found"));
        }
        return ResponseEntity.ok(Result.success(scooter));
    }

    @PostMapping("/add")
    @Operation(summary = "Add Scooter", description = "Admin adds a new scooter")
    public Map<String, Object> addScooter(@RequestBody Scooter scooter) {
        boolean saved = scooterService.save(scooter);
        return Map.of(
            "code", saved ? 200 : 400,
            "msg", saved ? "Success" : "Failed"
        );
    }
}
