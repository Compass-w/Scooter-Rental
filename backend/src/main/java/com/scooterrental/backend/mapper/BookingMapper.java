package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.Booking;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookingMapper {

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS planned_end_time TIMESTAMP")
    void addPlannedEndTimeColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS plan_type VARCHAR(24)")
    void addPlanTypeColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS payment_status VARCHAR(24) NOT NULL DEFAULT 'PENDING'")
    void addPaymentStatusColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS unlock_status VARCHAR(24) NOT NULL DEFAULT 'PENDING'")
    void addUnlockStatusColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS unlock_reference VARCHAR(64)")
    void addUnlockReferenceColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS overtime_fee_per_15_minutes DECIMAL(10, 2) NOT NULL DEFAULT 0")
    void addOvertimeFeeColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS overtime_charge_total DECIMAL(10, 2) NOT NULL DEFAULT 0")
    void addOvertimeChargeTotalColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS damage_charge_total DECIMAL(10, 2) NOT NULL DEFAULT 0")
    void addDamageChargeTotalColumn();

    @Update("ALTER TABLE bookings ADD COLUMN IF NOT EXISTS last_reminder_at TIMESTAMP")
    void addLastReminderAtColumn();

    // Get booking history for a specific user [ID: 8]
    @Select("SELECT b.booking_id AS bookingId, b.user_id AS userId, b.scooter_id AS scooterId, " +
            "b.start_time AS startTime, b.end_time AS endTime, b.total_cost AS totalCost, " +
            "b.duration_minutes AS durationMinutes, b.status, b.plan_type AS planType, " +
            "b.planned_end_time AS plannedEndTime, b.payment_status AS paymentStatus, " +
            "b.unlock_status AS unlockStatus, b.unlock_reference AS unlockReference, " +
            "b.overtime_fee_per_15_minutes AS overtimeFeePer15Minutes, " +
            "b.overtime_charge_total AS overtimeChargeTotal, b.damage_charge_total AS damageChargeTotal, " +
            "b.last_reminder_at AS lastReminderAt, b.created_at AS createdAt, " +
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

    @Insert("INSERT INTO bookings (user_id, scooter_id, start_time, planned_end_time, total_cost, duration_minutes, status, plan_type, payment_status, unlock_status, unlock_reference, overtime_fee_per_15_minutes, overtime_charge_total, damage_charge_total, last_reminder_at) " +
            "VALUES (#{userId}, #{scooterId}, #{startTime}, #{plannedEndTime}, #{totalCost}, #{durationMinutes}, #{status}, #{planType}, #{paymentStatus}, #{unlockStatus}, #{unlockReference}, #{overtimeFeePer15Minutes}, #{overtimeChargeTotal}, #{damageChargeTotal}, #{lastReminderAt})")
    @Options(useGeneratedKeys = true, keyProperty = "bookingId")
    int insertBooking(Booking booking);

    @Select("SELECT booking_id AS bookingId, user_id AS userId, scooter_id AS scooterId, " +
            "start_time AS startTime, end_time AS endTime, total_cost AS totalCost, " +
            "duration_minutes AS durationMinutes, status, plan_type AS planType, planned_end_time AS plannedEndTime, " +
            "payment_status AS paymentStatus, unlock_status AS unlockStatus, unlock_reference AS unlockReference, " +
            "overtime_fee_per_15_minutes AS overtimeFeePer15Minutes, overtime_charge_total AS overtimeChargeTotal, " +
            "damage_charge_total AS damageChargeTotal, last_reminder_at AS lastReminderAt, created_at AS createdAt " +
            "FROM bookings WHERE booking_id = #{bookingId}")
    Booking selectByBookingId(@Param("bookingId") Integer bookingId);

    @Select("SELECT booking_id AS bookingId, user_id AS userId, scooter_id AS scooterId, " +
            "start_time AS startTime, end_time AS endTime, total_cost AS totalCost, " +
            "duration_minutes AS durationMinutes, status, plan_type AS planType, planned_end_time AS plannedEndTime, " +
            "payment_status AS paymentStatus, unlock_status AS unlockStatus, unlock_reference AS unlockReference, " +
            "overtime_fee_per_15_minutes AS overtimeFeePer15Minutes, overtime_charge_total AS overtimeChargeTotal, " +
            "damage_charge_total AS damageChargeTotal, last_reminder_at AS lastReminderAt, created_at AS createdAt " +
            "FROM bookings WHERE user_id = #{userId} AND status = 'ACTIVE' " +
            "ORDER BY start_time DESC LIMIT 1")
    Booking selectActiveBookingByUserId(@Param("userId") Integer userId);

    @Select("SELECT b.booking_id AS bookingId, b.user_id AS userId, b.scooter_id AS scooterId, " +
            "b.start_time AS startTime, b.end_time AS endTime, b.total_cost AS totalCost, " +
            "b.duration_minutes AS durationMinutes, b.status, b.plan_type AS planType, b.planned_end_time AS plannedEndTime, " +
            "b.payment_status AS paymentStatus, b.unlock_status AS unlockStatus, b.unlock_reference AS unlockReference, " +
            "b.overtime_fee_per_15_minutes AS overtimeFeePer15Minutes, b.overtime_charge_total AS overtimeChargeTotal, " +
            "b.damage_charge_total AS damageChargeTotal, b.last_reminder_at AS lastReminderAt, b.created_at AS createdAt, " +
            "s.model AS scooterModel " +
            "FROM bookings b " +
            "LEFT JOIN scooters s ON b.scooter_id = s.scooter_id " +
            "ORDER BY b.created_at DESC")
    List<Booking> selectAllForAdmin();

    @Select("SELECT booking_id AS bookingId, user_id AS userId, scooter_id AS scooterId, " +
            "start_time AS startTime, end_time AS endTime, total_cost AS totalCost, " +
            "duration_minutes AS durationMinutes, status, plan_type AS planType, planned_end_time AS plannedEndTime, " +
            "payment_status AS paymentStatus, unlock_status AS unlockStatus, unlock_reference AS unlockReference, " +
            "overtime_fee_per_15_minutes AS overtimeFeePer15Minutes, overtime_charge_total AS overtimeChargeTotal, " +
            "damage_charge_total AS damageChargeTotal, last_reminder_at AS lastReminderAt, created_at AS createdAt " +
            "FROM bookings WHERE status = 'ACTIVE' ORDER BY start_time ASC")
    List<Booking> selectActiveBookings();

    @Update("UPDATE bookings SET end_time = #{endTime}, total_cost = #{totalCost}, " +
            "duration_minutes = #{durationMinutes}, status = #{status}, payment_status = #{paymentStatus}, " +
            "unlock_status = #{unlockStatus}, overtime_charge_total = #{overtimeChargeTotal}, " +
            "damage_charge_total = #{damageChargeTotal}, last_reminder_at = #{lastReminderAt} " +
            "WHERE booking_id = #{bookingId}")
    int completeBooking(Booking booking);

    @Update("UPDATE bookings SET duration_minutes = #{durationMinutes}, total_cost = #{totalCost} " +
            "WHERE booking_id = #{bookingId} AND status = 'ACTIVE'")
    int extendActiveBooking(Booking booking);

    @Update("""
            UPDATE bookings
            SET total_cost = #{totalCost},
                payment_status = #{paymentStatus},
                unlock_status = #{unlockStatus},
                unlock_reference = #{unlockReference},
                overtime_fee_per_15_minutes = #{overtimeFeePer15Minutes},
                overtime_charge_total = #{overtimeChargeTotal},
                damage_charge_total = #{damageChargeTotal},
                last_reminder_at = #{lastReminderAt},
                planned_end_time = #{plannedEndTime}
            WHERE booking_id = #{bookingId}
            """)
    int updateAutomationState(Booking booking);
}
