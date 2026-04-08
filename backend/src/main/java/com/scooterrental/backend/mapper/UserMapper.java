package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

        // Find user by username for login
        @Select("SELECT user_id AS userId, username, email, phone, city, avatar_url AS avatarUrl, password_hash AS passwordHash, role, " +
                        "total_riding_minutes AS totalRidingMinutes, achievements, created_at AS createdAt " +
                        "FROM users WHERE username = #{username}")
        User findByUsername(@Param("username") String username);

        // Register: Insert new user
        @Insert("INSERT INTO users(username, email, phone, city, avatar_url, password_hash, role) " +
                        "VALUES(#{username}, #{email}, #{phone}, #{city}, #{avatarUrl}, #{passwordHash}, 'customer')")
        @Options(useGeneratedKeys = true, keyProperty = "userId")
        void insert(User user);

        // Get user profile by ID
        @Select("SELECT user_id AS userId, username, email, phone, city, avatar_url AS avatarUrl, role, " +
                        "total_riding_minutes AS totalRidingMinutes, achievements, created_at AS createdAt " +
                        "FROM users WHERE user_id = #{userId}")
        User selectById(@Param("userId") Integer userId);

        /**
         * Update user profile information (email and phone) [ID: user.js]
         */
        @Update("UPDATE users SET " +
                        "username = COALESCE(#{username}, username), " +
                        "email = COALESCE(#{email}, email), " +
                        "phone = COALESCE(#{phone}, phone), " +
                        "city = COALESCE(#{city}, city), " +
                        "avatar_url = COALESCE(#{avatarUrl}, avatar_url) " +
                        "WHERE user_id = #{userId}")
        int updateUser(User user);

        // Update achievements for gamification [ID: 22]
        @Update("UPDATE users SET achievements = #{achievements} WHERE user_id = #{userId}")
        void updateAchievements(@Param("userId") Integer userId, @Param("achievements") String achievements);
}
