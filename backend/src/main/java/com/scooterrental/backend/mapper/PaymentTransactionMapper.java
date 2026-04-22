package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.PaymentTransaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PaymentTransactionMapper {

    @Update("""
            CREATE TABLE IF NOT EXISTS payment_transactions (
                transaction_id SERIAL PRIMARY KEY,
                booking_id INTEGER,
                user_id INTEGER,
                scooter_id INTEGER,
                gateway_provider VARCHAR(64) NOT NULL DEFAULT 'SIMULATED_GATEWAY',
                transaction_type VARCHAR(24) NOT NULL,
                charge_category VARCHAR(32) NOT NULL,
                amount DECIMAL(10, 2) NOT NULL,
                currency VARCHAR(8) NOT NULL DEFAULT 'CNY',
                status VARCHAR(24) NOT NULL,
                gateway_reference VARCHAR(64) NOT NULL,
                detail TEXT,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                processed_at TIMESTAMP
            )
            """)
    void createTableIfNotExists();

    @Insert("""
            INSERT INTO payment_transactions (
                booking_id,
                user_id,
                scooter_id,
                gateway_provider,
                transaction_type,
                charge_category,
                amount,
                currency,
                status,
                gateway_reference,
                detail,
                processed_at
            )
            VALUES (
                #{bookingId},
                #{userId},
                #{scooterId},
                #{gatewayProvider},
                #{transactionType},
                #{chargeCategory},
                #{amount},
                #{currency},
                #{status},
                #{gatewayReference},
                #{detail},
                #{processedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionId")
    int insert(PaymentTransaction transaction);

    @Select("""
            SELECT transaction_id AS transactionId,
                   booking_id AS bookingId,
                   user_id AS userId,
                   scooter_id AS scooterId,
                   gateway_provider AS gatewayProvider,
                   transaction_type AS transactionType,
                   charge_category AS chargeCategory,
                   amount,
                   currency,
                   status,
                   gateway_reference AS gatewayReference,
                   detail,
                   created_at AS createdAt,
                   processed_at AS processedAt
            FROM payment_transactions
            ORDER BY created_at DESC, transaction_id DESC
            LIMIT #{limit}
            """)
    List<PaymentTransaction> selectRecent(@Param("limit") int limit);

    @Select("""
            SELECT COALESCE(SUM(amount), 0)
            FROM payment_transactions
            WHERE booking_id = #{bookingId}
              AND status = 'CAPTURED'
            """)
    java.math.BigDecimal sumCapturedAmountByBooking(@Param("bookingId") Integer bookingId);
}
