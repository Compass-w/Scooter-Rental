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
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Insert("""
            INSERT INTO issue_reports (user_id, scooter_id, booking_id, category, description, status)
            VALUES (#{userId}, #{scooterId}, #{bookingId}, #{category}, #{description}, #{status})
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
                   created_at AS createdAt
            FROM issue_reports
            ORDER BY created_at DESC, issue_id DESC
            """)
    List<IssueReport> selectAll();
}
