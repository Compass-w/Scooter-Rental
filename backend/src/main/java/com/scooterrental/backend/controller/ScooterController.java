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

    // 1. 获取所有滑板车 (对应前端请求 /api/scooters)
    // 💡 修改点：去掉了 "/list"，直接映射根路径
    @GetMapping
    @Operation(summary = "Get Scooter List", description = "Returns a list of all scooters")
    public Map<String, Object> getAllScooters() {
        List<Map<String, Object>> scooters = new ArrayList<>();

        // 💡 修改点：字段名严格对齐了 find-scooter.vue 里的属性需求
        Map<String, Object> s1 = new HashMap<>();
        s1.put("id", 101);
        s1.put("model", "Ninebot Max");
        s1.put("latitude", 53.801277);   // 前端需要 latitude 而不是 lat
        s1.put("longitude", -1.548567);  // 前端需要 longitude 而不是 lng
        s1.put("status", "AVAILABLE");   // 前端判断逻辑用的是全大写 AVAILABLE
        s1.put("batteryLevel", 85);      // 前端需要 batteryLevel 而不是 battery
        s1.put("basePrice", 1.00);
        s1.put("pricePerMin", 0.15);
        s1.put("location", "Leeds City Centre");

        // 额外加一条“使用中”的测试数据，方便你在前端看不同的 UI 状态
        Map<String, Object> s2 = new HashMap<>();
        s2.put("id", 102);
        s2.put("model", "Xiaomi Pro 2");
        s2.put("latitude", 53.805277);
        s2.put("longitude", -1.543567);
        s2.put("status", "IN_USE"); 
        s2.put("batteryLevel", 42);
        s2.put("basePrice", 1.00);
        s2.put("pricePerMin", 0.15);
        s2.put("location", "University Campus");

        scooters.add(s1);
        scooters.add(s2);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", scooters);
        response.put("msg", "Success");
        return response;
    }

    // 2. 获取可用滑板车兜底接口 (对应前端请求 /api/scooters/available)
    // 💡 修改点：新增了这个接口，防止 getAllScooters 失败时前端 fallback 请求报错 404
    @GetMapping("/available")
    @Operation(summary = "Get Available Scooters", description = "Returns only available scooters")
    public Map<String, Object> getAvailableScooters() {
        List<Map<String, Object>> scooters = new ArrayList<>();

        Map<String, Object> s1 = new HashMap<>();
        s1.put("id", 101);
        s1.put("model", "Ninebot Max");
        s1.put("latitude", 53.801277);
        s1.put("longitude", -1.548567);
        s1.put("status", "AVAILABLE");
        s1.put("batteryLevel", 85);
        s1.put("basePrice", 1.00);
        s1.put("pricePerMin", 0.15);
        s1.put("location", "Leeds City Centre");

        scooters.add(s1);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", scooters);
        response.put("msg", "Success");
        return response;
    }

    // 3. 添加滑板车 (管理员端)
    @PostMapping("/add")
    @Operation(summary = "Add Scooter", description = "Admin adds a new scooter")
    public Map<String, Object> addScooter(@RequestBody Map<String, Object> scooterData) {
        return Map.of("code", 200, "msg", "Scooter added successfully (Mock)");
    }
}