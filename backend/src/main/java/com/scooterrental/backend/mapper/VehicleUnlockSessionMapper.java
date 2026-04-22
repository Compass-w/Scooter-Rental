package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.VehicleUnlockSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VehicleUnlockSessionMapper {

    @Update("""
            CREATE TABLE IF NOT EXISTS vehicle_unlock_sessions (
                command_id SERIAL PRIMARY KEY,
                booking_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                scooter_id INTEGER NOT NULL,
                scan_token VARCHAR(120) NOT NULL,
                command_status VARCHAR(24) NOT NULL,
                communication_status VARCHAR(24) NOT NULL,
                telemetry_status VARCHAR(24) NOT NULL,
                lock_state VARCHAR(24) NOT NULL,
                device_message TEXT,
                acknowledged_at TIMESTAMP,
                last_heartbeat_at TIMESTAMP,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Insert("""
            INSERT INTO vehicle_unlock_sessions (
                booking_id,
                user_id,
                scooter_id,
                scan_token,
                command_status,
                communication_status,
                telemetry_status,
                lock_state,
                device_message,
                acknowledged_at,
                last_heartbeat_at,
                updated_at
            )
            VALUES (
                #{bookingId},
                #{userId},
                #{scooterId},
                #{scanToken},
                #{commandStatus},
                #{communicationStatus},
                #{telemetryStatus},
                #{lockState},
                #{deviceMessage},
                #{acknowledgedAt},
                #{lastHeartbeatAt},
                #{updatedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "commandId")
    int insert(VehicleUnlockSession session);

    @Update("""
            UPDATE vehicle_unlock_sessions
            SET command_status = #{commandStatus},
                communication_status = #{communicationStatus},
                telemetry_status = #{telemetryStatus},
                lock_state = #{lockState},
                device_message = #{deviceMessage},
                acknowledged_at = #{acknowledgedAt},
                updated_at = #{updatedAt}
            WHERE command_id = #{commandId}
            """)
    int updateAcknowledgement(VehicleUnlockSession session);

    @Update("""
            UPDATE vehicle_unlock_sessions
            SET lock_state = #{lockState},
                telemetry_status = #{telemetryStatus},
                last_heartbeat_at = #{lastHeartbeatAt},
                device_message = COALESCE(#{deviceMessage}, device_message),
                updated_at = #{updatedAt}
            WHERE booking_id = #{bookingId}
            """)
    int updateHeartbeatByBooking(
            @Param("bookingId") Integer bookingId,
            @Param("lockState") String lockState,
            @Param("telemetryStatus") String telemetryStatus,
            @Param("lastHeartbeatAt") LocalDateTime lastHeartbeatAt,
            @Param("deviceMessage") String deviceMessage,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE vehicle_unlock_sessions
            SET command_status = #{commandStatus},
                lock_state = #{lockState},
                telemetry_status = #{telemetryStatus},
                device_message = #{deviceMessage},
                updated_at = #{updatedAt}
            WHERE booking_id = #{bookingId}
            """)
    int completeByBooking(
            @Param("bookingId") Integer bookingId,
            @Param("commandStatus") String commandStatus,
            @Param("lockState") String lockState,
            @Param("telemetryStatus") String telemetryStatus,
            @Param("deviceMessage") String deviceMessage,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Select("""
            SELECT command_id AS commandId,
                   booking_id AS bookingId,
                   user_id AS userId,
                   scooter_id AS scooterId,
                   scan_token AS scanToken,
                   command_status AS commandStatus,
                   communication_status AS communicationStatus,
                   telemetry_status AS telemetryStatus,
                   lock_state AS lockState,
                   device_message AS deviceMessage,
                   acknowledged_at AS acknowledgedAt,
                   last_heartbeat_at AS lastHeartbeatAt,
                   created_at AS createdAt,
                   updated_at AS updatedAt
            FROM vehicle_unlock_sessions
            ORDER BY created_at DESC, command_id DESC
            LIMIT #{limit}
            """)
    List<VehicleUnlockSession> selectRecent(@Param("limit") int limit);
}
