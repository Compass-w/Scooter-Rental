package com.scooterrental.backend.service;

import com.scooterrental.backend.dto.booking.StartRideRequest;
import com.scooterrental.backend.dto.booking.StartRideResponse;
import com.scooterrental.backend.entity.AutomationEvent;
import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.entity.VehicleUnlockSession;
import com.scooterrental.backend.mapper.AutomationEventMapper;
import com.scooterrental.backend.mapper.BookingMapper;
import com.scooterrental.backend.mapper.ScooterMapper;
import com.scooterrental.backend.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ScooterMapper scooterMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private VehicleIntegrationService vehicleIntegrationService;

    @Mock
    private PaymentGatewayService paymentGatewayService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AutomationEventMapper automationEventMapper;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(
                bookingMapper,
                scooterMapper,
                userMapper,
                vehicleIntegrationService,
                paymentGatewayService,
                notificationService,
                automationEventMapper);
    }

    @Test
    void startRideAppliesStudentDiscountAndRecordsConfirmation() {
        Scooter scooter = availableScooter();
        User student = user(7, "student7", "student7@leeds.ac.uk", 0, "");
        VehicleUnlockSession unlockSession = new VehicleUnlockSession();
        unlockSession.setCommandId(77);
        unlockSession.setLockState("UNLOCKED");
        unlockSession.setDeviceMessage("Vehicle acknowledged the unlock command and opened the lock.");

        when(bookingMapper.selectActiveBookingByUserId(7)).thenReturn(null);
        when(scooterMapper.selectById(3)).thenReturn(scooter);
        when(userMapper.selectById(7)).thenReturn(student);
        when(bookingMapper.insertBooking(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setBookingId(101);
            return 1;
        });
        when(scooterMapper.updateStatusIfCurrent(3, "AVAILABLE", "IN_USE")).thenReturn(1);
        when(vehicleIntegrationService.dispatchUnlockCommand(any(Booking.class), eq(scooter), eq("scan-101")))
                .thenReturn(unlockSession);
        when(notificationService.sendRideBookingConfirmationEmail(any(Booking.class), eq(student), eq("student7@leeds.ac.uk")))
                .thenReturn(true);

        StartRideResponse response = bookingService.startRide(startRequest());

        assertThat(response.getEstimatedCost()).isEqualByComparingTo("10.20");
        assertThat(response.getPaymentStatus()).isEqualTo("AUTHORIZED");
        assertThat(response.getUnlockStatus()).isEqualTo("UNLOCKED");
        assertThat(response.getDeviceMessage()).contains("Student 4 hours discount applied");

        ArgumentCaptor<BigDecimal> authorisedAmount = ArgumentCaptor.forClass(BigDecimal.class);
        verify(paymentGatewayService).authorizeStartRide(any(Booking.class), authorisedAmount.capture(), anyString());
        assertThat(authorisedAmount.getValue()).isEqualByComparingTo("10.20");

        ArgumentCaptor<AutomationEvent> eventCaptor = ArgumentCaptor.forClass(AutomationEvent.class);
        verify(automationEventMapper, org.mockito.Mockito.atLeastOnce()).insert(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .extracting(AutomationEvent::getEventType)
                .contains("BOOKING_CONFIRMATION", "BOOKING_DISCOUNT");
    }

    @Test
    void startRideFailsWhenScooterWasTakenByAnotherRequest() {
        when(bookingMapper.selectActiveBookingByUserId(7)).thenReturn(null);
        when(scooterMapper.selectById(3)).thenReturn(availableScooter());
        when(userMapper.selectById(7)).thenReturn(user(7, "rider", "rider@example.com", 0, ""));
        when(bookingMapper.insertBooking(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setBookingId(102);
            return 1;
        });
        when(scooterMapper.updateStatusIfCurrent(3, "AVAILABLE", "IN_USE")).thenReturn(0);

        assertThatThrownBy(() -> bookingService.startRide(startRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Scooter is not available");

        verify(paymentGatewayService, never()).authorizeStartRide(any(), any(), anyString());
        verify(vehicleIntegrationService, never()).dispatchUnlockCommand(any(), any(), anyString());
    }

    @Test
    void cancelActiveBookingReleasesScooterAndRecordsAudit() {
        Booking booking = activeBooking();
        when(bookingMapper.selectByBookingId(101)).thenReturn(booking);
        when(bookingMapper.cancelBooking(101, 7)).thenReturn(1);

        boolean cancelled = bookingService.cancel(101, 7);

        assertThat(cancelled).isTrue();
        verify(vehicleIntegrationService).completeRideSession(101);
        verify(scooterMapper).updateStatus(3, "AVAILABLE");
        ArgumentCaptor<AutomationEvent> eventCaptor = ArgumentCaptor.forClass(AutomationEvent.class);
        verify(automationEventMapper).insert(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo("BOOKING_CANCELLED");
    }

    @Test
    void endRideAppliesFrequentRiderDiscountAndUpdatesRidingMinutes() {
        Booking booking = activeBooking();
        booking.setDurationMinutes(60);
        booking.setStartTime(LocalDateTime.now().minusMinutes(60));
        booking.setPlanType("1_HOUR");
        booking.setEstimatedReturnBattery(80);
        booking.setStartBatteryLevel(90);
        booking.setElectricityChargeTotal(BigDecimal.ZERO);

        User frequentUser = user(7, "regular", "regular@example.com", 480, "");

        when(bookingMapper.selectByBookingId(101)).thenReturn(booking);
        when(scooterMapper.selectById(3)).thenReturn(availableScooter());
        when(userMapper.selectById(7)).thenReturn(frequentUser);

        bookingService.endRide(101, Map.of("returnBatteryLevel", 90));

        ArgumentCaptor<BigDecimal> capturedBaseCharge = ArgumentCaptor.forClass(BigDecimal.class);
        verify(paymentGatewayService).captureBaseCharge(any(Booking.class), capturedBaseCharge.capture(), anyString());
        assertThat(capturedBaseCharge.getValue()).isEqualByComparingTo("3.15");
        verify(userMapper).incrementTotalRidingMinutes(7, 540);
        verify(vehicleIntegrationService).completeRideSession(101);
    }

    private StartRideRequest startRequest() {
        StartRideRequest request = new StartRideRequest();
        request.setUserId(7);
        request.setScooterId(3);
        request.setPlanType("4_HOURS");
        request.setScanToken("scan-101");
        request.setLiabilityAccepted(true);
        return request;
    }

    private Scooter availableScooter() {
        Scooter scooter = new Scooter();
        scooter.setScooterId(3L);
        scooter.setModel("Ninebot Max");
        scooter.setStatus("AVAILABLE");
        scooter.setBatteryLevel(90);
        scooter.setBasePrice(BigDecimal.valueOf(1));
        scooter.setPricePerMin(BigDecimal.valueOf(0.20));
        return scooter;
    }

    private Booking activeBooking() {
        Booking booking = new Booking();
        booking.setBookingId(101);
        booking.setUserId(7);
        booking.setScooterId(3);
        booking.setStatus("ACTIVE");
        booking.setStartTime(LocalDateTime.now().minusMinutes(10));
        booking.setDurationMinutes(60);
        booking.setPlanType("1_HOUR");
        booking.setPaymentStatus("AUTHORIZED");
        booking.setUnlockStatus("UNLOCKED");
        booking.setOvertimeChargeTotal(BigDecimal.ZERO);
        booking.setDamageChargeTotal(BigDecimal.ZERO);
        booking.setElectricityChargeTotal(BigDecimal.ZERO);
        return booking;
    }

    private User user(Integer userId, String username, String email, Integer ridingMinutes, String achievements) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setTotalRidingMinutes(ridingMinutes);
        user.setAchievements(achievements);
        return user;
    }
}
