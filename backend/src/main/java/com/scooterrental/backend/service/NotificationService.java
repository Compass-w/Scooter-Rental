package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.StaffBooking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter DELIVERY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final RestClient.Builder restClientBuilder;

    @Value("${app.notifications.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.notifications.email.from:}")
    private String emailFrom;

    @Value("${app.notifications.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.notifications.twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${app.notifications.twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${app.notifications.twilio.from-number:}")
    private String twilioFromNumber;

    @Value("${app.password-reset-url-template:http://localhost:3000/#/pages/reset-password?token={token}}")
    private String passwordResetUrlTemplate;

    public NotificationService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            RestClient.Builder restClientBuilder) {
        this.mailSenderProvider = mailSenderProvider;
        this.restClientBuilder = restClientBuilder;
    }

    public String buildPasswordResetLink(String token) {
        return passwordResetUrlTemplate.replace("{token}", token == null ? "" : token);
    }

    public boolean sendPasswordResetEmail(String recipientEmail, String resetLink, LocalDateTime expiresAt) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            return false;
        }

        String subject = "ScooterGo password reset";
        String body = String.format(
                "We received a password reset request for your ScooterGo account.%n%n"
                        + "Reset your password using the secure link below:%n%s%n%n"
                        + "This link expires at %s.%n"
                        + "If you did not request this change, you can ignore this email.",
                resetLink,
                formatDateTime(expiresAt));
        return sendEmail(recipientEmail, subject, body);
    }

    public boolean sendPasswordResetSms(String phoneNumber, String email, String resetLink, LocalDateTime expiresAt) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return false;
        }

        String message = String.format(
                "ScooterGo reset requested for %s. Link: %s (expires %s)",
                email == null || email.isBlank() ? "your account" : email,
                resetLink,
                formatDateTime(expiresAt));
        return sendSms(phoneNumber, message);
    }

    public boolean sendStaffBookingConfirmationEmail(StaffBooking booking, String recipientEmail) {
        if (booking == null || recipientEmail == null || recipientEmail.isBlank()) {
            return false;
        }

        String estimatedCost = booking.getEstimatedCost() == null
                ? "TBD"
                : formatMoney(booking.getEstimatedCost());
        String body = String.format(
                "Hi %s,%n%n"
                        + "Your ScooterGo booking has been confirmed.%n%n"
                        + "Booking ID: %s%n"
                        + "Scooter: %s%n"
                        + "Hire period: %s%n"
                        + "Pickup store: %s%n"
                        + "Return store: %s%n"
                        + "Estimated cost: %s%n%n"
                        + "Thank you for choosing ScooterGo.",
                defaultText(booking.getGuestName(), "Customer"),
                defaultText(booking.getBookingId(), "-"),
                defaultText(booking.getScooterModel(), "Assigned at pickup"),
                defaultText(booking.getHirePeriod(), "Standard"),
                defaultText(booking.getPickupStoreName(), "ScooterGo store"),
                defaultText(booking.getReturnStoreName(), "ScooterGo store"),
                estimatedCost);
        return sendEmail(recipientEmail, "Your ScooterGo booking confirmation", body);
    }

    private boolean sendEmail(String recipientEmail, String subject, String body) {
        if (!emailEnabled) {
            log.warn("Email delivery skipped because app.notifications.email.enabled=false");
            return false;
        }
        if (emailFrom == null || emailFrom.isBlank()) {
            log.warn("Email delivery skipped because app.notifications.email.from is empty");
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Email delivery skipped because JavaMailSender is not configured");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email notification to {}", recipientEmail, ex);
            return false;
        }
    }

    private boolean sendSms(String phoneNumber, String messageBody) {
        if (!smsEnabled) {
            return false;
        }
        if (isBlank(twilioAccountSid) || isBlank(twilioAuthToken) || isBlank(twilioFromNumber)) {
            log.warn("SMS delivery skipped because Twilio credentials are incomplete");
            return false;
        }

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("From", twilioFromNumber);
            formData.add("To", phoneNumber);
            formData.add("Body", messageBody);

            restClientBuilder
                    .baseUrl("https://api.twilio.com")
                    .build()
                    .post()
                    .uri("/2010-04-01/Accounts/{accountSid}/Messages.json", twilioAccountSid)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .headers(headers -> headers.setBasicAuth(twilioAccountSid, twilioAuthToken))
                    .body(formData)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception ex) {
            log.error("Failed to send SMS notification to {}", phoneNumber, ex);
            return false;
        }
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "soon" : DELIVERY_TIME_FORMATTER.format(value);
    }

    private String formatMoney(BigDecimal value) {
        return "RMB " + value.stripTrailingZeros().toPlainString();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String defaultText(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? fallback : text;
    }
}
