package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.IssueReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IssueReportMapper {

    @Update("""
            CREATE TABLE IF NOT EXISTS issue_reports (
                issue_id SERIAL PRIMARY KEY,
                user_id INTEGER,
                scooter_id INTEGER NOT NULL,
                booking_id INTEGER,
                category VARCHAR(32) NOT NULL,
                description TEXT NOT NULL,
                status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
                priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM',
                workflow_status VARCHAR(24) NOT NULL DEFAULT 'REPORTED',
                assigned_staff VARCHAR(80),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Update("ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM'")
    void ensurePriorityColumn();

    @Update("ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS workflow_status VARCHAR(24) NOT NULL DEFAULT 'REPORTED'")
    void ensureWorkflowStatusColumn();

    @Update("ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS assigned_staff VARCHAR(80)")
    void ensureAssignedStaffColumn();

    @Update("ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    void ensureUpdatedAtColumn();

    @Insert("""
            INSERT INTO issue_reports (
                user_id,
                scooter_id,
                booking_id,
                category,
                description,
                status,
                priority,
                workflow_status,
                assigned_staff,
                updated_at
            )
            VALUES (
                #{userId},
                #{scooterId},
                #{bookingId},
                #{category},
                #{description},
                #{status},
                #{priority},
                #{workflowStatus},
                #{assignedStaff},
                #{updatedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "issueId")
    int insert(IssueReport issueReport);

    @Select("""
            SELECT issue_id AS issueId,
                   user_id AS userId,
                   scooter_id AS scooterId,
                   booking_id AS bookingId,
                   category,
                   description,
                   status,
                   priority,
                   workflow_status AS workflowStatus,
                   assigned_staff AS assignedStaff,
                   created_at AS createdAt,
                   updated_at AS updatedAt
            FROM issue_reports
            ORDER BY created_at DESC, issue_id DESC
            """)
    List<IssueReport> selectAll();

    @Select("""
            SELECT issue_id AS issueId,
                   user_id AS userId,
                   scooter_id AS scooterId,
                   booking_id AS bookingId,
                   category,
                   description,
                   status,
                   priority,
                   workflow_status AS workflowStatus,
                   assigned_staff AS assignedStaff,
                   created_at AS createdAt,
                   updated_at AS updatedAt
            FROM issue_reports
            WHERE issue_id = #{issueId}
            """)
    IssueReport selectById(Integer issueId);

    @Update("""
            UPDATE issue_reports
            SET priority = #{priority},
                workflow_status = #{workflowStatus},
                assigned_staff = #{assignedStaff},
                status = #{status},
                updated_at = #{updatedAt}
            WHERE issue_id = #{issueId}
            """)
    int updateAdminFields(IssueReport issueReport);
}
