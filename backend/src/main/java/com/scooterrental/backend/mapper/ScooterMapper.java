package com.scooterrental.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scooterrental.backend.entity.Scooter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Mapper interface for Scooter entity.
 * Extends BaseMapper to inherit standard CRUD operations from MyBatis-Plus.
 * No XML file is needed because we use MyBatis-Plus built-in methods.
 */
@Mapper
public interface ScooterMapper extends BaseMapper<Scooter> {
    @Update("""
            UPDATE scooters
            SET status = #{nextStatus}
            WHERE scooter_id = #{scooterId}
              AND UPPER(status) = UPPER(#{expectedCurrentStatus})
            """)
    int updateStatusIfCurrent(
            @Param("scooterId") Integer scooterId,
            @Param("expectedCurrentStatus") String expectedCurrentStatus,
            @Param("nextStatus") String nextStatus);

    @Update("""
            UPDATE scooters
            SET status = #{nextStatus}
            WHERE scooter_id = #{scooterId}
            """)
    int updateStatus(
            @Param("scooterId") Integer scooterId,
            @Param("nextStatus") String nextStatus);
}
