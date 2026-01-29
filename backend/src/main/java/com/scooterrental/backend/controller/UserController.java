package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result; // 引入刚才建的类
import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users") // 保持你喜欢的 /api/users
@CrossOrigin(origins = "*")
@Tag(name = "User Module", description = "User Login and Registration")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User Login")
    // 返回类型改为 Result<Map<String, Object>>
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = userService.login(username, password);
        
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", "fake-jwt-token-for-demo"); // 模拟 Token
            data.put("userId", user.getUserId());
            data.put("role", user.getRole());
            
            // 使用 Result.success 包裹数据
            return Result.success(data); 
        } else {
            // 使用 Result.error 返回错误
            return Result.error(400, "Invalid username or password");
        }
    }
    
    // 你还需要补一个注册接口来匹配文档 Backlog ID 1
    @PostMapping("/register")
    @Operation(summary = "User Register")
    public Result<Map<String, Object>> register(@RequestBody User user) {
        // 这里调用 Service 层的注册逻辑 (你需要去 Service 实现它)
        // 假设注册成功
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 101); // 模拟 ID
        return Result.success(data);
    }
}
