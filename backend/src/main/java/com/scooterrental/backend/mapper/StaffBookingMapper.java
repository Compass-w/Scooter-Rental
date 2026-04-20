package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.StaffBooking;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StaffBookingMapper {

    @Update("""
            CREATE TABLE IF NOT EXISTS staff_bookings (
                booking_id SERIAL PRIMARY KEY,
                guest_name VARCHAR(120) NOT NULL,
                guest_email VARCHAR(160),
                scooter_id INTEGER NOT NULL,
                hire_period VARCHAR(24) NOT NULL,
                desired_start_time TIMESTAMP NOT NULL,
                estimated_cost DECIMAL(10, 2) NOT NULL,
                booking_status VARCHAR(24) NOT NULL DEFAULT 'BOOKED',
                confirmation_sent_at TIMESTAMP,
                notes TEXT,
                created_by VARCHAR(80) NOT NULL DEFAULT 'Front Desk',
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Insert("""
            INSERT INTO staff_bookings (
                guest_name,
                guest_email,
                scooter_id,
                hire_period,
                desired_start_time,
                estimated_cost,
                booking_status,
                confirmation_sent_at,
                notes,
                created_by
            )
            VALUES (
                #{guestName},
                #{guestEmail},
                #{scooterId},
                #{hirePeriod},
                #{desiredStartTime},
                #{estimatedCost},
                #{bookingStatus},
                #{confirmationSentAt},
                #{notes},
                #{createdBy}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "bookingId")
    int insert(StaffBooking staffBooking);

    @Select("""
            SELECT sb.booking_id AS bookingId,
                   sb.guest_name AS guestName,
                   sb.guest_email AS guestEmail,
                   sb.scooter_id AS scooterId,
                   s.model AS scooterModel,
                   sb.hire_period AS hirePeriod,
                   sb.desired_start_time AS desiredStartTime,
                   sb.estimated_cost AS estimatedCost,
                   sb.booking_status AS bookingStatus,
                   sb.confirmation_sent_at AS confirmationSentAt,
                   sb.notes,
                   sb.created_by AS createdBy,
                   sb.created_at AS createdAt
            FROM staff_bookings sb
            LEFT JOIN scooters s ON sb.scooter_id = s.scooter_id
            ORDER BY sb.created_at DESC, sb.booking_id DESC
            """)
    List<StaffBooking> selectAll();

    @Select("""
            SELECT sb.booking_id AS bookingId,
                   sb.guest_name AS guestName,
                   sb.guest_email AS guestEmail,
                   sb.scooter_id AS scooterId,
                   s.model AS scooterModel,
                   sb.hire_period AS hirePeriod,
                   sb.desired_start_time AS desiredStartTime,
                   sb.estimated_cost AS estimatedCost,
                   sb.booking_status AS bookingStatus,
                   sb.confirmation_sent_at AS confirmationSentAt,
                   sb.notes,
                   sb.created_by AS createdBy,
                   sb.created_at AS createdAt
            FROM staff_bookings sb
            LEFT JOIN scooters s ON sb.scooter_id = s.scooter_id
            WHERE sb.booking_id = #{bookingId}
            """)
    StaffBooking selectById(Integer bookingId);

    @Update("""
            UPDATE staff_bookings
            SET guest_email = COALESCE(#{guestEmail}, guest_email),
                confirmation_sent_at = #{confirmationSentAt},
                booking_status = #{bookingStatus}
            WHERE booking_id = #{bookingId}
            """)
    int updateConfirmationStatus(
            @Param("bookingId") Integer bookingId,
            @Param("guestEmail") String guestEmail,
            @Param("confirmationSentAt") LocalDateTime confirmationSentAt,
            @Param("bookingStatus") String bookingStatus);
}
