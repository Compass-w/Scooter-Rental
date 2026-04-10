package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.IssueReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
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
                priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM',
                status VARCHAR(16) NOT NULL DEFAULT 'REPORTED',
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Update("ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM'")
    void addPriorityColumnIfNotExists();

    @Update("ALTER TABLE issue_reports ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    void addUpdatedAtColumnIfNotExists();

    @Update("""
            UPDATE issue_reports
            SET status = 'REPORTED'
            WHERE status IS NULL OR UPPER(status) = 'OPEN'
            """)
    void migrateLegacyStatuses();

    @Update("""
            UPDATE issue_reports
            SET priority = CASE
                WHEN category IN ('MECHANICAL', 'BATTERY') THEN 'HIGH'
                WHEN category = 'ELECTRICAL' THEN 'MEDIUM'
                ELSE 'LOW'
            END
            WHERE priority IS NULL OR TRIM(priority) = ''
            """)
    void backfillPriorityValues();

    @Update("""
            UPDATE issue_reports
            SET updated_at = COALESCE(updated_at, created_at, CURRENT_TIMESTAMP)
            WHERE updated_at IS NULL
            """)
    void backfillUpdatedAtValues();

    @Insert("""
            INSERT INTO issue_reports (user_id, scooter_id, booking_id, category, description, priority, status, updated_at)
            VALUES (#{userId}, #{scooterId}, #{bookingId}, #{category}, #{description}, #{priority}, #{status}, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "issueId")
    int insert(IssueReport issueReport);

    @Select({
            "<script>",
            "SELECT issue_id AS issueId,",
            "       user_id AS userId,",
            "       scooter_id AS scooterId,",
            "       booking_id AS bookingId,",
            "       category,",
            "       description,",
            "       priority,",
            "       status,",
            "       created_at AS createdAt,",
            "       updated_at AS updatedAt",
            "FROM issue_reports",
            "<if test='priority != null and priority != \"\"'>",
            "WHERE UPPER(priority) = #{priority}",
            "</if>",
            "ORDER BY CASE UPPER(priority)",
            "         WHEN 'HIGH' THEN 1",
            "         WHEN 'MEDIUM' THEN 2",
            "         ELSE 3 END,",
            "         created_at DESC,",
            "         issue_id DESC",
            "</script>"
    })
    List<IssueReport> selectByPriority(@Param("priority") String priority);

    @Update("""
            UPDATE issue_reports
            SET status = #{status},
                updated_at = CURRENT_TIMESTAMP
            WHERE issue_id = #{issueId}
            """)
    int updateStatus(@Param("issueId") Integer issueId, @Param("status") String status);
}
