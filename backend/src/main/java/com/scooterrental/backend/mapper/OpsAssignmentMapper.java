package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.OpsAssignment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OpsAssignmentMapper {

    @Update("""
            CREATE TABLE IF NOT EXISTS ops_assignments (
                assignment_id SERIAL PRIMARY KEY,
                staff_name VARCHAR(120) NOT NULL,
                staff_role VARCHAR(32) NOT NULL,
                zone_label VARCHAR(120) NOT NULL,
                shift_status VARCHAR(24) NOT NULL DEFAULT 'READY',
                tasks_in_queue INTEGER NOT NULL DEFAULT 0,
                assigned_scooters INTEGER NOT NULL DEFAULT 0,
                contact_phone VARCHAR(40),
                notes TEXT,
                last_seen_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Select("""
            SELECT assignment_id AS assignmentId,
                   staff_name AS staffName,
                   staff_role AS staffRole,
                   zone_label AS zoneLabel,
                   shift_status AS shiftStatus,
                   tasks_in_queue AS tasksInQueue,
                   assigned_scooters AS assignedScooters,
                   contact_phone AS contactPhone,
                   notes,
                   last_seen_at AS lastSeenAt,
                   created_at AS createdAt
            FROM ops_assignments
            ORDER BY created_at DESC, assignment_id DESC
            """)
    List<OpsAssignment> selectAll();

    @Select("""
            SELECT assignment_id AS assignmentId,
                   staff_name AS staffName,
                   staff_role AS staffRole,
                   zone_label AS zoneLabel,
                   shift_status AS shiftStatus,
                   tasks_in_queue AS tasksInQueue,
                   assigned_scooters AS assignedScooters,
                   contact_phone AS contactPhone,
                   notes,
                   last_seen_at AS lastSeenAt,
                   created_at AS createdAt
            FROM ops_assignments
            WHERE assignment_id = #{assignmentId}
            """)
    OpsAssignment selectById(@Param("assignmentId") Integer assignmentId);

    @Select("SELECT COUNT(*) FROM ops_assignments")
    int countAll();

    @Insert("""
            INSERT INTO ops_assignments (
                staff_name,
                staff_role,
                zone_label,
                shift_status,
                tasks_in_queue,
                assigned_scooters,
                contact_phone,
                notes,
                last_seen_at
            )
            VALUES (
                #{staffName},
                #{staffRole},
                #{zoneLabel},
                #{shiftStatus},
                #{tasksInQueue},
                #{assignedScooters},
                #{contactPhone},
                #{notes},
                #{lastSeenAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "assignmentId")
    int insert(OpsAssignment assignment);

    @Update("""
            UPDATE ops_assignments
            SET shift_status = #{shiftStatus},
                tasks_in_queue = COALESCE(#{tasksInQueue}, tasks_in_queue),
                assigned_scooters = COALESCE(#{assignedScooters}, assigned_scooters),
                last_seen_at = #{lastSeenAt},
                notes = COALESCE(#{notes}, notes)
            WHERE assignment_id = #{assignmentId}
            """)
    int updateStatus(
            @Param("assignmentId") Integer assignmentId,
            @Param("shiftStatus") String shiftStatus,
            @Param("tasksInQueue") Integer tasksInQueue,
            @Param("assignedScooters") Integer assignedScooters,
            @Param("lastSeenAt") LocalDateTime lastSeenAt,
            @Param("notes") String notes);
}
