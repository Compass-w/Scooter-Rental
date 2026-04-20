package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.MaintenanceLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MaintenanceLogMapper {

    @Update("""
            CREATE TABLE IF NOT EXISTS maintenance_logs (
                log_id SERIAL PRIMARY KEY,
                scooter_id INTEGER NOT NULL,
                technician_name VARCHAR(80) NOT NULL,
                action_taken VARCHAR(120) NOT NULL,
                notes TEXT,
                battery_level INTEGER,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Insert("""
            INSERT INTO maintenance_logs (scooter_id, technician_name, action_taken, notes, battery_level)
            VALUES (#{scooterId}, #{technicianName}, #{actionTaken}, #{notes}, #{batteryLevel})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "logId")
    int insert(MaintenanceLog maintenanceLog);

    @Select("""
            SELECT log_id AS logId,
                   scooter_id AS scooterId,
                   technician_name AS technicianName,
                   action_taken AS actionTaken,
                   notes,
                   battery_level AS batteryLevel,
                   created_at AS createdAt
            FROM maintenance_logs
            WHERE scooter_id = #{scooterId}
            ORDER BY created_at DESC, log_id DESC
            """)
    List<MaintenanceLog> selectByScooterId(Integer scooterId);

    @Select("""
            SELECT log_id AS logId,
                   scooter_id AS scooterId,
                   technician_name AS technicianName,
                   action_taken AS actionTaken,
                   notes,
                   battery_level AS batteryLevel,
                   created_at AS createdAt
            FROM maintenance_logs
            ORDER BY created_at DESC, log_id DESC
            LIMIT 40
            """)
    List<MaintenanceLog> selectRecent();
}
