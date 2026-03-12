package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

        @Select("SELECT user_id AS userId, username, email, phone, password_hash AS passwordHash, role, " +
                        "total_riding_minutes AS totalRidingMinutes, achievements, created_at AS createdAt " +
                        "FROM users WHERE username = #{username}")
        User findByUsername(@Param("username") String username);

        @Insert("INSERT INTO users(username, email, phone, password_hash, role) " +
                        "VALUES(#{username}, #{email}, #{phone}, #{passwordHash}, 'customer')")
        @Options(useGeneratedKeys = true, keyProperty = "userId")
        void insert(User user);

        @Select("SELECT user_id AS userId, username, email, phone, role, " +
                        "total_riding_minutes AS totalRidingMinutes, achievements, created_at AS createdAt " +
                        "FROM users WHERE user_id = #{userId}")
        User selectById(@Param("userId") Integer userId);

        // --- Updated for updateUserInfo API ---
        @Update("UPDATE users SET email = #{email}, phone = #{phone} WHERE user_id = #{userId}")
        int updateProfile(User user);

        @Update("UPDATE users SET achievements = #{achievements} WHERE user_id = #{userId}")
        void updateAchievements(@Param("userId") Integer userId, @Param("achievements") String achievements);
}
