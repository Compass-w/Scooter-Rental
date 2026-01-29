package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 登录用：查找用户
    @Select("SELECT user_id AS userId, username, email, phone, password_hash AS passwordHash, role, created_at AS createdAt "
            +
            "FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    // --- 新增：注册用 (插入用户) ---
    // 注意：这里加入了 phone 字段
    @Insert("INSERT INTO users(username, email, phone, password_hash, role) " +
            "VALUES(#{username}, #{email}, #{phone}, #{passwordHash}, 'customer')")
    @Options(useGeneratedKeys = true, keyProperty = "userId") // 让 MyBatis 把生成的 ID 回填给 User 对象
    void insert(User user);
}
