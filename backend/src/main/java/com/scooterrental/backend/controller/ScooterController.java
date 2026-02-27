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
@CrossOrigin(origins = "*")
@Tag(name = "Scooter Module", description = "Manage scooter status and location")
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    // 1. 获取所有滑板车 (对应前端 /api/scooters)
    // 综合点：使用了 feature 分支要求的根路径映射，但内部调用 main 分支的数据库逻辑
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

    // 2. 获取可用滑板车 (对应前端 /api/scooters/available)
    @GetMapping("/available")
    @Operation(summary = "Get Available Scooters", description = "Returns only available scooters")
    public Map<String, Object> getAvailableScooters() {
        // 调用 main 分支实现的数据库筛选逻辑
        List<Scooter> scooters = scooterService.getAvailableScooters(null, null);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", scooters);
        response.put("msg", "Success");
        return response;
    }

    // 3. 添加滑板车 (管理员端)
    @PostMapping("/add")
    @Operation(summary = "Add Scooter", description = "Admin adds a new scooter")
    public Map<String, Object> addScooter(@RequestBody Scooter scooter) {
        boolean saved = scooterService.save(scooter);
        return Map.of("code", saved ? 200 : 400, "msg", saved ? "Success" : "Failed");
    }
}