package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 查找用户 (使用别名映射数据库字段: password_hash -> passwordHash)
    @Select("SELECT user_id AS userId, username, email, password_hash AS passwordHash, role, created_at AS createdAt " +
            "FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
}
