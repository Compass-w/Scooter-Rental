package com.scooterrental.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scooterrental.backend.entity.Scooter;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for Scooter entity.
 * Extends BaseMapper to inherit standard CRUD operations from MyBatis-Plus.
 * No XML file is needed because we use MyBatis-Plus built-in methods.
 */
@Mapper
public interface ScooterMapper extends BaseMapper<Scooter> {
    // No explicit SQL needed for basic operations.
    // MyBatis-Plus automatically handles insert, update, delete, and select.
}
