package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // Login: Find user by username
    @Select("SELECT user_id AS userId, username, email, phone, password_hash AS passwordHash, role, created_at AS createdAt "
            +
            "FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    // Register: Insert new user
    @Insert("INSERT INTO users(username, email, phone, password_hash, role) " +
            "VALUES(#{username}, #{email}, #{phone}, #{passwordHash}, 'customer')")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insert(User user);

    // --- NEW: For User Profile (Get user by ID) ---
    @Select("SELECT user_id AS userId, username, email, phone, role, created_at AS createdAt " +
            "FROM users WHERE user_id = #{userId}")
    User selectById(@Param("userId") Integer userId);
}
