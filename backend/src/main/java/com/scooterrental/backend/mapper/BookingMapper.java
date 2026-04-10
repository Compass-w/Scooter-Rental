package com.scooterrental.backend.mapper;

import com.scooterrental.backend.dto.admin.IncomeReportPoint;
import com.scooterrental.backend.entity.Booking;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface BookingMapper {

    // Get booking history for a specific user [ID: 8]
    @Select("SELECT b.booking_id AS bookingId, b.user_id AS userId, b.scooter_id AS scooterId, " +
            "b.start_time AS startTime, b.end_time AS endTime, b.total_cost AS totalCost, " +
            "b.duration_minutes AS durationMinutes, b.status, b.created_at AS createdAt, " +
            "s.model AS scooterModel " + // Added scooter model for receipt
            "FROM bookings b " +
            "LEFT JOIN scooters s ON b.scooter_id = s.scooter_id " +
            "WHERE b.user_id = #{userId} ORDER BY b.created_at DESC")
    List<Booking> selectByUserId(Integer userId);

    // Cancel a booking if status is PENDING [ID: 12]
    @Update("UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = #{bookingId} AND user_id = #{userId} AND status = 'PENDING'")
    int cancelBooking(@Param("bookingId") Integer bookingId, @Param("userId") Integer userId);

    // Get weekly usage stats [ID: 22]
    @Select("SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as day, SUM(duration_minutes) as minutes " +
            "FROM bookings WHERE user_id = #{userId} AND status = 'COMPLETED' AND created_at > NOW() - INTERVAL '7 days' " +
            "GROUP BY day ORDER BY day")
    List<Map<String, Object>> getWeeklyStats(Integer userId);

    @Select("""
            SELECT TO_CHAR(DATE_TRUNC('day', COALESCE(end_time, created_at)), 'YYYY-MM-DD') AS label,
                   COALESCE(SUM(total_cost), 0) AS revenue,
                   COUNT(*) AS bookingCount
            FROM bookings
            WHERE status = 'COMPLETED'
              AND COALESCE(end_time, created_at) >= #{startDateTime}
            GROUP BY DATE_TRUNC('day', COALESCE(end_time, created_at))
            ORDER BY DATE_TRUNC('day', COALESCE(end_time, created_at))
            """)
    List<IncomeReportPoint> selectRevenueByDay(@Param("startDateTime") LocalDateTime startDateTime);

    @Select("""
            SELECT TO_CHAR(DATE_TRUNC('week', COALESCE(end_time, created_at)), 'YYYY-MM-DD') AS label,
                   COALESCE(SUM(total_cost), 0) AS revenue,
                   COUNT(*) AS bookingCount
            FROM bookings
            WHERE status = 'COMPLETED'
              AND COALESCE(end_time, created_at) >= #{startDateTime}
            GROUP BY DATE_TRUNC('week', COALESCE(end_time, created_at))
            ORDER BY DATE_TRUNC('week', COALESCE(end_time, created_at))
            """)
    List<IncomeReportPoint> selectRevenueByWeek(@Param("startDateTime") LocalDateTime startDateTime);

    @Insert("INSERT INTO bookings (user_id, scooter_id, start_time, total_cost, duration_minutes, status) " +
            "VALUES (#{userId}, #{scooterId}, #{startTime}, #{totalCost}, #{durationMinutes}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "bookingId")
    int insertBooking(Booking booking);

    @Select("SELECT booking_id AS bookingId, user_id AS userId, scooter_id AS scooterId, " +
            "start_time AS startTime, end_time AS endTime, total_cost AS totalCost, " +
            "duration_minutes AS durationMinutes, status, created_at AS createdAt " +
            "FROM bookings WHERE booking_id = #{bookingId}")
    Booking selectByBookingId(@Param("bookingId") Integer bookingId);

    @Select("SELECT booking_id AS bookingId, user_id AS userId, scooter_id AS scooterId, " +
            "start_time AS startTime, end_time AS endTime, total_cost AS totalCost, " +
            "duration_minutes AS durationMinutes, status, created_at AS createdAt " +
            "FROM bookings WHERE user_id = #{userId} AND status = 'ACTIVE' " +
            "ORDER BY start_time DESC LIMIT 1")
    Booking selectActiveBookingByUserId(@Param("userId") Integer userId);

    @Update("UPDATE bookings SET end_time = #{endTime}, total_cost = #{totalCost}, " +
            "duration_minutes = #{durationMinutes}, status = #{status} " +
            "WHERE booking_id = #{bookingId}")
    int completeBooking(Booking booking);

    @Update("UPDATE bookings SET duration_minutes = #{durationMinutes}, total_cost = #{totalCost} " +
            "WHERE booking_id = #{bookingId} AND status = 'ACTIVE'")
    int extendActiveBooking(Booking booking);
}
