package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.entity.PaymentTransaction;
import com.scooterrental.backend.mapper.PaymentTransactionMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentGatewayService {

    private static final String GATEWAY_PROVIDER = "SIMULATED_GATEWAY";
    private static final String CURRENCY = "CNY";

    private final PaymentTransactionMapper paymentTransactionMapper;

    public PaymentGatewayService(PaymentTransactionMapper paymentTransactionMapper) {
        this.paymentTransactionMapper = paymentTransactionMapper;
    }

    public void ensureGatewayTables() {
        paymentTransactionMapper.createTableIfNotExists();
    }

    public PaymentTransaction authorizeStartRide(Booking booking, BigDecimal amount, String detail) {
        return createTransaction(booking, "AUTH", "START_RIDE", amount, "AUTHORIZED", detail);
    }

    public PaymentTransaction captureBaseCharge(Booking booking, BigDecimal amount, String detail) {
        return createTransaction(booking, "CAPTURE", "END_RIDE", amount, "CAPTURED", detail);
    }

    public PaymentTransaction captureAdjustment(Booking booking, BigDecimal amount, String category, String detail) {
        return createTransaction(booking, "CAPTURE", category, amount, "CAPTURED", detail);
    }

    public List<PaymentTransaction> getRecentTransactions(int limit) {
        ensureGatewayTables();
        return paymentTransactionMapper.selectRecent(Math.max(1, limit));
    }

    public BigDecimal getCapturedTotal(Integer bookingId) {
        ensureGatewayTables();
        return normalizeMoney(paymentTransactionMapper.sumCapturedAmountByBooking(bookingId));
    }

    private PaymentTransaction createTransaction(
            Booking booking,
            String transactionType,
            String chargeCategory,
            BigDecimal amount,
            String status,
            String detail) {
        ensureGatewayTables();

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setBookingId(booking.getBookingId());
        transaction.setUserId(booking.getUserId());
        transaction.setScooterId(booking.getScooterId());
        transaction.setGatewayProvider(GATEWAY_PROVIDER);
        transaction.setTransactionType(transactionType);
        transaction.setChargeCategory(chargeCategory);
        transaction.setAmount(normalizeMoney(amount));
        transaction.setCurrency(CURRENCY);
        transaction.setStatus(status);
        transaction.setGatewayReference(buildReference(transactionType, chargeCategory));
        transaction.setDetail(detail);
        transaction.setProcessedAt(LocalDateTime.now());
        paymentTransactionMapper.insert(transaction);
        return transaction;
    }

    private String buildReference(String transactionType, String chargeCategory) {
        String prefix = (transactionType + "-" + chargeCategory).replace('_', '-');
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }
}
