package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.User;
import com.scooterrental.backend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    void loginAcceptsLegacyPlaintextPasswordAndUpgradesHash() {
        User legacyUser = new User();
        legacyUser.setUsername("legacy-user");
        legacyUser.setEmail("legacy-user@example.com");
        legacyUser.setPasswordHash("legacy-pass");
        userMapper.insert(legacyUser);

        User authenticatedUser = userService.login("legacy-user", "legacy-pass");

        assertThat(authenticatedUser).isNotNull();

        User reloadedUser = userMapper.findByUsername("legacy-user");
        assertThat(reloadedUser.getPasswordHash()).startsWith("$2");
    }
}
