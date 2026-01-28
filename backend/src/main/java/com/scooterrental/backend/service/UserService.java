package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.findByUsername(username);

        // 2. 简单密码比对 (后期可升级为 BCrypt)
        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }

        return null;
    }
}
