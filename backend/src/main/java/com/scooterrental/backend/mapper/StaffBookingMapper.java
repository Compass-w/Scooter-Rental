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
                booking_channel VARCHAR(32) NOT NULL DEFAULT 'WALK_IN_COUNTER',
                pickup_store_code VARCHAR(64),
                pickup_store_name VARCHAR(120),
                return_store_code VARCHAR(64),
                return_store_name VARCHAR(120),
                pickup_battery_level INTEGER,
                expected_return_battery_level INTEGER,
                electricity_delta DECIMAL(10, 2) NOT NULL DEFAULT 0,
                card_holder_name VARCHAR(120),
                card_number_masked VARCHAR(20),
                card_expiry VARCHAR(8),
                payment_status VARCHAR(24) NOT NULL DEFAULT 'CARD_BOUND',
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

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS booking_channel VARCHAR(32) NOT NULL DEFAULT 'WALK_IN_COUNTER'")
    void addBookingChannelColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS pickup_store_code VARCHAR(64)")
    void addPickupStoreCodeColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS pickup_store_name VARCHAR(120)")
    void addPickupStoreNameColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS return_store_code VARCHAR(64)")
    void addReturnStoreCodeColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS return_store_name VARCHAR(120)")
    void addReturnStoreNameColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS pickup_battery_level INTEGER")
    void addPickupBatteryLevelColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS expected_return_battery_level INTEGER")
    void addExpectedReturnBatteryLevelColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS electricity_delta DECIMAL(10, 2) NOT NULL DEFAULT 0")
    void addElectricityDeltaColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS card_holder_name VARCHAR(120)")
    void addCardHolderNameColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS card_number_masked VARCHAR(20)")
    void addCardNumberMaskedColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS card_expiry VARCHAR(8)")
    void addCardExpiryColumn();

    @Update("ALTER TABLE staff_bookings ADD COLUMN IF NOT EXISTS payment_status VARCHAR(24) NOT NULL DEFAULT 'CARD_BOUND'")
    void addPaymentStatusColumn();

    @Insert("""
            INSERT INTO staff_bookings (
                guest_name,
                guest_email,
                scooter_id,
                hire_period,
                booking_channel,
                pickup_store_code,
                pickup_store_name,
                return_store_code,
                return_store_name,
                pickup_battery_level,
                expected_return_battery_level,
                electricity_delta,
                card_holder_name,
                card_number_masked,
                card_expiry,
                payment_status,
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
                #{bookingChannel},
                #{pickupStoreCode},
                #{pickupStoreName},
                #{returnStoreCode},
                #{returnStoreName},
                #{pickupBatteryLevel},
                #{expectedReturnBatteryLevel},
                #{electricityDelta},
                #{cardHolderName},
                #{cardNumberMasked},
                #{cardExpiry},
                #{paymentStatus},
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
                   sb.booking_channel AS bookingChannel,
                   sb.pickup_store_code AS pickupStoreCode,
                   sb.pickup_store_name AS pickupStoreName,
                   sb.return_store_code AS returnStoreCode,
                   sb.return_store_name AS returnStoreName,
                   sb.pickup_battery_level AS pickupBatteryLevel,
                   sb.expected_return_battery_level AS expectedReturnBatteryLevel,
                   sb.electricity_delta AS electricityDelta,
                   sb.card_holder_name AS cardHolderName,
                   sb.card_number_masked AS cardNumberMasked,
                   sb.card_expiry AS cardExpiry,
                   sb.payment_status AS paymentStatus,
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
                   sb.booking_channel AS bookingChannel,
                   sb.pickup_store_code AS pickupStoreCode,
                   sb.pickup_store_name AS pickupStoreName,
                   sb.return_store_code AS returnStoreCode,
                   sb.return_store_name AS returnStoreName,
                   sb.pickup_battery_level AS pickupBatteryLevel,
                   sb.expected_return_battery_level AS expectedReturnBatteryLevel,
                   sb.electricity_delta AS electricityDelta,
                   sb.card_holder_name AS cardHolderName,
                   sb.card_number_masked AS cardNumberMasked,
                   sb.card_expiry AS cardExpiry,
                   sb.payment_status AS paymentStatus,
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
