package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.AutomationEvent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AutomationEventMapper {

    @Update("""
            CREATE TABLE IF NOT EXISTS automation_events (
                event_id SERIAL PRIMARY KEY,
                booking_id INTEGER,
                issue_id INTEGER,
                event_type VARCHAR(40) NOT NULL,
                status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
                amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                detail TEXT,
                due_at TIMESTAMP,
                processed_at TIMESTAMP,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Insert("""
            INSERT INTO automation_events (
                booking_id,
                issue_id,
                event_type,
                status,
                amount,
                detail,
                due_at,
                processed_at
            )
            VALUES (
                #{bookingId},
                #{issueId},
                #{eventType},
                #{status},
                #{amount},
                #{detail},
                #{dueAt},
                #{processedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "eventId")
    int insert(AutomationEvent event);

    @Select("""
            SELECT event_id AS eventId,
                   booking_id AS bookingId,
                   issue_id AS issueId,
                   event_type AS eventType,
                   status,
                   amount,
                   detail,
                   due_at AS dueAt,
                   processed_at AS processedAt,
                   created_at AS createdAt
            FROM automation_events
            ORDER BY created_at DESC, event_id DESC
            LIMIT #{limit}
            """)
    List<AutomationEvent> selectRecent(@Param("limit") int limit);

    @Select("""
            SELECT COUNT(*)
            FROM automation_events
            WHERE booking_id = #{bookingId}
              AND event_type = #{eventType}
              AND status = 'COMPLETED'
            """)
    int countCompletedByBookingAndType(@Param("bookingId") Integer bookingId, @Param("eventType") String eventType);

    @Select("""
            SELECT COUNT(*)
            FROM automation_events
            WHERE booking_id = #{bookingId}
              AND issue_id = #{issueId}
              AND event_type = #{eventType}
              AND status = 'COMPLETED'
            """)
    int countCompletedByBookingIssueAndType(
            @Param("bookingId") Integer bookingId,
            @Param("issueId") Integer issueId,
            @Param("eventType") String eventType);
}
