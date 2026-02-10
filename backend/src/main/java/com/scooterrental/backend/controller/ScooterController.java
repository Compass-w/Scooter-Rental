package com.scooterrental.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/scooters")
@CrossOrigin(origins = "*")
@Tag(name = "Scooter Module", description = "Manage scooter status and location")
public class ScooterController {

    // 1. 获取滑板车列表 (用户端/地图页使用)
    @GetMapping("/list")
    @Operation(summary = "Get Scooter List", description = "Returns a list of available scooters with location")
    public Map<String, Object> getScooterList() {
        // --- 模拟假数据 (Mock Data) ---
        List<Map<String, Object>> scooters = new ArrayList<>();

        Map<String, Object> s1 = new HashMap<>();
        s1.put("id", 101);
        s1.put("lat", 53.801277);
        s1.put("lng", -1.548567);
        s1.put("status", "available");
        s1.put("battery", 85);

        scooters.add(s1);
        // ---------------------------

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", scooters);
        response.put("msg", "Success");
        return response;
    }

    // 2. 添加滑板车 (管理员端)
    @PostMapping("/add")
    @Operation(summary = "Add Scooter", description = "Admin adds a new scooter")
    public Map<String, Object> addScooter(@RequestBody Map<String, Object> scooterData) {
        return Map.of("code", 200, "msg", "Scooter added successfully (Mock)");
    }
}
