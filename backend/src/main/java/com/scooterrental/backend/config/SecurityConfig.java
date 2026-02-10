package com.scooterrental.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 1. 密码加密器 (保留你原来的，非常正确)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 2. 安全过滤链配置 (重点修改了这里)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF (REST API 不需要)
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ 新增: 开启跨域支持 (CORS)
                // 如果不加这一行，前端会报 "CORS error"
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 配置路径拦截规则
                .authorizeHttpRequests(auth -> auth
                        // 放行注册和登录接口 (必须!)
                        .requestMatchers("/api/auth/**").permitAll()
                        // 放行 Swagger 文档 (方便你调试)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 放行查询滑板车 (允许未登录看地图)
                        .requestMatchers("/api/scooters/available").permitAll()

                        // ⚠️ 暂时全部放行 (方便你和前端先调通)
                        // 等你们 Token 功能写好了，再改成 .anyRequest().authenticated()
                        .anyRequest().permitAll());

        return http.build();
    }

    /**
     * ✅ 新增: 全局跨域配置源
     * 允许前端从任何端口 (localhost:xxxx) 访问你的后端
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许带 Cookie/Token
        config.addAllowedOriginPattern("*"); // 允许所有来源
        config.addAllowedHeader("*"); // 允许所有请求头
        config.addAllowedMethod("*"); // 允许所有方法 (GET, POST, PUT, DELETE)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
