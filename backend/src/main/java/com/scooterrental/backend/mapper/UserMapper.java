package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(100)")
        void addEmailColumn();

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20)")
        void addPhoneColumn();

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS city VARCHAR(100)")
        void addCityColumn();

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url TEXT")
        void addAvatarUrlColumn();

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'customer'")
        void addRoleColumn();

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS total_riding_minutes INT DEFAULT 0")
        void addTotalRidingMinutesColumn();

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS achievements TEXT DEFAULT ''")
        void addAchievementsColumn();

        @Update("ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        void addCreatedAtColumn();

        @Update("""
                        UPDATE users
                        SET email = COALESCE(email, 'manager1@leeds.ac.uk'),
                            phone = COALESCE(phone, '07123450001'),
                            city = COALESCE(city, 'Leeds'),
                            password_hash = '$2y$10$o9w2LkXNfi6dgHUIlrMxNuiiUhnxwRlxJ9NwMTLZWGI9ImY64rD4K',
                            role = 'manager'
                        WHERE username = 'manager1'
                        """)
        int updateDefaultManagerAccount();

        @Insert("""
                        INSERT INTO users (
                            username,
                            email,
                            phone,
                            city,
                            password_hash,
                            role,
                            total_riding_minutes,
                            achievements
                        )
                        SELECT
                            'manager1',
                            'manager1@leeds.ac.uk',
                            '07123450001',
                            'Leeds',
                            '$2y$10$o9w2LkXNfi6dgHUIlrMxNuiiUhnxwRlxJ9NwMTLZWGI9ImY64rD4K',
                            'manager',
                            12,
                            ''
                        WHERE NOT EXISTS (
                            SELECT 1 FROM users WHERE username = 'manager1'
                        )
                        """)
        int insertDefaultManagerAccountIfMissing();

        @Select("SELECT user_id AS userId, username, email, phone, city, avatar_url AS avatarUrl, role, " +
                        "total_riding_minutes AS totalRidingMinutes, achievements, created_at AS createdAt " +
                        "FROM users ORDER BY created_at DESC, user_id DESC")
        java.util.List<User> selectAll();

        // Find user by username for login
        @Select("SELECT user_id AS userId, username, email, phone, city, avatar_url AS avatarUrl, password_hash AS passwordHash, role, " +
                        "total_riding_minutes AS totalRidingMinutes, achievements, created_at AS createdAt " +
                        "FROM users WHERE username = #{username}")
        User findByUsername(@Param("username") String username);

        @Select("SELECT user_id AS userId, username, email, phone, city, avatar_url AS avatarUrl, password_hash AS passwordHash, role, " +
                        "total_riding_minutes AS totalRidingMinutes, achievements, created_at AS createdAt " +
                        "FROM users WHERE LOWER(email) = LOWER(#{email}) " +
                        "ORDER BY user_id DESC LIMIT 1")
        User findByEmail(@Param("email") String email);

        @Select("SELECT COUNT(1) FROM users WHERE LOWER(email) = LOWER(#{email}) AND user_id <> #{userId}")
        int countByEmailExcludingUserId(@Param("email") String email, @Param("userId") Integer userId);

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

        @Update("UPDATE users SET password_hash = #{passwordHash} WHERE user_id = #{userId}")
        int updatePasswordHash(@Param("userId") Integer userId, @Param("passwordHash") String passwordHash);
}
