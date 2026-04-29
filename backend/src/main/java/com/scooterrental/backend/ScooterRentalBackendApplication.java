package com.scooterrental.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableScheduling
@MapperScan("com.scooterrental.backend.mapper") // 扫描 Mapper 接口
public class ScooterRentalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScooterRentalBackendApplication.class, args);
    }

}
