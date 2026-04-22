package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.entity.VehicleUnlockSession;
import com.scooterrental.backend.mapper.VehicleUnlockSessionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VehicleIntegrationService {

    private final VehicleUnlockSessionMapper vehicleUnlockSessionMapper;

    public VehicleIntegrationService(VehicleUnlockSessionMapper vehicleUnlockSessionMapper) {
        this.vehicleUnlockSessionMapper = vehicleUnlockSessionMapper;
    }

    public void ensureVehicleTables() {
        vehicleUnlockSessionMapper.createTableIfNotExists();
    }

    public VehicleUnlockSession dispatchUnlockCommand(Booking booking, Scooter scooter, String scanToken) {
        ensureVehicleTables();

        VehicleUnlockSession session = new VehicleUnlockSession();
        session.setBookingId(booking.getBookingId());
        session.setUserId(booking.getUserId());
        session.setScooterId(booking.getScooterId());
        session.setScanToken(scanToken);
        session.setCommandStatus("SENT");
        session.setCommunicationStatus(resolveCommunicationStatus(scooter));
        session.setTelemetryStatus(resolveTelemetryStatus(scooter));
        session.setLockState("LOCKED");
        session.setDeviceMessage("Unlock command queued from QR scan.");
        session.setUpdatedAt(LocalDateTime.now());
        vehicleUnlockSessionMapper.insert(session);

        if (!"CONNECTED".equals(session.getCommunicationStatus())) {
            throw new IllegalStateException("Scooter communication module is offline. Please rescan or choose another vehicle.");
        }

        LocalDateTime acknowledgedAt = LocalDateTime.now();
        session.setCommandStatus("ACKNOWLEDGED");
        session.setTelemetryStatus("LIVE");
        session.setLockState("UNLOCKED");
        session.setDeviceMessage("Vehicle acknowledged the unlock command and opened the lock.");
        session.setAcknowledgedAt(acknowledgedAt);
        session.setLastHeartbeatAt(acknowledgedAt);
        session.setUpdatedAt(acknowledgedAt);
        vehicleUnlockSessionMapper.updateAcknowledgement(session);
        return session;
    }

    public void recordTelemetry(Integer bookingId, String lockState, String deviceMessage) {
        ensureVehicleTables();
        LocalDateTime now = LocalDateTime.now();
        vehicleUnlockSessionMapper.updateHeartbeatByBooking(
                bookingId,
                lockState == null || lockState.isBlank() ? "UNLOCKED" : lockState.trim().toUpperCase(),
                "LIVE",
                now,
                deviceMessage,
                now);
    }

    public void completeRideSession(Integer bookingId) {
        ensureVehicleTables();
        vehicleUnlockSessionMapper.completeByBooking(
                bookingId,
                "COMPLETED",
                "LOCKED",
                "IDLE",
                "Ride completed and lock closed.",
                LocalDateTime.now());
    }

    public java.util.List<VehicleUnlockSession> getRecentSessions(int limit) {
        ensureVehicleTables();
        return vehicleUnlockSessionMapper.selectRecent(Math.max(1, limit));
    }

    private String resolveCommunicationStatus(Scooter scooter) {
        Integer batteryLevel = scooter.getBatteryLevel();
        if (batteryLevel != null && batteryLevel <= 8) {
            return "OFFLINE";
        }
        return "CONNECTED";
    }

    private String resolveTelemetryStatus(Scooter scooter) {
        Integer batteryLevel = scooter.getBatteryLevel();
        if (batteryLevel != null && batteryLevel <= 15) {
            return "DEGRADED";
        }
        return "LIVE";
    }
}
