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
                safety_action TEXT,
                insurance_case_status VARCHAR(40) NOT NULL DEFAULT 'NOT_REQUIRED',
                customer_charge_policy TEXT,
                repair_charge_estimate DECIMAL(10, 2) NOT NULL DEFAULT 0,
                rider_injured BOOLEAN NOT NULL DEFAULT FALSE,
                third_party_involved BOOLEAN NOT NULL DEFAULT FALSE,
                emergency_services_contacted BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

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
                safety_action,
                insurance_case_status,
                customer_charge_policy,
                repair_charge_estimate,
                rider_injured,
                third_party_involved,
                emergency_services_contacted,
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
                #{safetyAction},
                #{insuranceCaseStatus},
                #{customerChargePolicy},
                #{repairChargeEstimate},
                #{riderInjured},
                #{thirdPartyInvolved},
                #{emergencyServicesContacted},
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
                   safety_action AS safetyAction,
                   insurance_case_status AS insuranceCaseStatus,
                   customer_charge_policy AS customerChargePolicy,
                   repair_charge_estimate AS repairChargeEstimate,
                   rider_injured AS riderInjured,
                   third_party_involved AS thirdPartyInvolved,
                   emergency_services_contacted AS emergencyServicesContacted,
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
                   safety_action AS safetyAction,
                   insurance_case_status AS insuranceCaseStatus,
                   customer_charge_policy AS customerChargePolicy,
                   repair_charge_estimate AS repairChargeEstimate,
                   rider_injured AS riderInjured,
                   third_party_involved AS thirdPartyInvolved,
                   emergency_services_contacted AS emergencyServicesContacted,
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
