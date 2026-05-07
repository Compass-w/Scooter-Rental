package com.scooterrental.backend.controller;

import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.service.AuthSessionService;
import com.scooterrental.backend.service.NotificationService;
import com.scooterrental.backend.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthSessionService authSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        when(notificationService.buildPasswordResetLink(ArgumentMatchers.anyString()))
                .thenAnswer(invocation -> "http://localhost:3000/#/pages/reset-password?token=" + invocation.getArgument(0));
        when(notificationService.sendPasswordResetEmail(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any()))
                .thenReturn(true);
        when(notificationService.sendPasswordResetSms(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any()))
                .thenReturn(false);
    }

    @Test
    void loginReturnsSessionTokenAndNormalizedRole() throws Exception {
        User user = new User();
        user.setUsername("login-user");
        user.setEmail("login-user@example.com");
        user.setPasswordHash("LoginPass1!");
        userService.register(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"login-user","password":"LoginPass1!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value(startsWith("session-")))
                .andExpect(jsonPath("$.data.username").value("login-user"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }

    @Test
    void loginAcceptsSeededManagerAccount() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"manager1","password":"Tyz114031!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value(startsWith("session-")))
                .andExpect(jsonPath("$.data.username").value("manager1"))
                .andExpect(jsonPath("$.data.role").value("MANAGER"));
    }

    @Test
    void secondLoginInvalidatesPreviousSessionForSameAccount() throws Exception {
        User user = new User();
        user.setUsername("single-session-user");
        user.setEmail("single-session-user@example.com");
        user.setPasswordHash("LoginPass1!");
        userService.register(user);

        String firstToken = loginAndExtractToken("single-session-user", "LoginPass1!");
        String secondToken = loginAndExtractToken("single-session-user", "LoginPass1!");

        mockMvc.perform(get("/api/users/" + user.getUserId())
                        .header("Authorization", "Bearer " + firstToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/users/" + user.getUserId())
                        .header("Authorization", "Bearer " + secondToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("single-session-user"));
    }

    @Test
    void logoutRevokesCurrentSessionToken() throws Exception {
        User customer = new User();
        customer.setUserId(1);
        customer.setUsername("student1");
        customer.setEmail("student1@leeds.ac.uk");
        customer.setRole("customer");
        String token = authSessionService.createSession(customer);

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void forgotPasswordSendsEmailWithoutExposingResetToken() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"student1@leeds.ac.uk"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("student1@leeds.ac.uk"))
                .andExpect(jsonPath("$.data.emailSent").value(true))
                .andExpect(jsonPath("$.data.smsSent").value(false))
                .andExpect(jsonPath("$.data.resetToken").doesNotExist())
                .andExpect(jsonPath("$.data.resetPath").doesNotExist());

        verify(notificationService).sendPasswordResetEmail(
                ArgumentMatchers.eq("student1@leeds.ac.uk"),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any());
    }

    @Test
    void forgotPasswordFallsBackToManualResetLinkWhenEmailDeliveryIsUnavailable() throws Exception {
        when(notificationService.sendPasswordResetEmail(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any()))
                .thenReturn(false);
        when(notificationService.sendPasswordResetSms(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any()))
                .thenReturn(false);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"student1@leeds.ac.uk"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("student1@leeds.ac.uk"))
                .andExpect(jsonPath("$.data.manualResetAvailable").value(true))
                .andExpect(jsonPath("$.data.resetLink").exists())
                .andExpect(jsonPath("$.data.resetPath").exists());
    }

    @Test
    void resetPasswordAcceptsIssuedTokenAndAllowsLoginWithNewPassword() throws Exception {
        UserService.PasswordResetRequestData resetRequest = userService.createPasswordReset("student1@leeds.ac.uk", null);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","newPassword":"BetterPass1!"}
                                """.formatted(resetRequest.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"student1","password":"BetterPass1!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("student1"));
    }

    @Test
    void adminEndpointsRejectCustomerSessions() throws Exception {
        User customer = new User();
        customer.setUserId(1);
        customer.setUsername("student1");
        customer.setEmail("student1@leeds.ac.uk");
        customer.setRole("customer");
        String token = authSessionService.createSession(customer);

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void userProfileRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void customerCanReadOwnProfile() throws Exception {
        User customer = new User();
        customer.setUserId(1);
        customer.setUsername("student1");
        customer.setEmail("student1@leeds.ac.uk");
        customer.setRole("customer");
        String token = authSessionService.createSession(customer);

        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("student1"));
    }

    @Test
    void customerCannotReadAnotherUsersProfile() throws Exception {
        User customer = new User();
        customer.setUserId(1);
        customer.setUsername("student1");
        customer.setEmail("student1@leeds.ac.uk");
        customer.setRole("customer");
        String token = authSessionService.createSession(customer);

        mockMvc.perform(get("/api/users/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String loginAndExtractToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }
}
