package com.example.demo.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testGenerateAndValidateToken() {
        User user = new User("testuser", "password", User.Role.PATIENT);
        user.setId("test-id");

        String token = jwtUtils.generateToken(user);
        assertNotNull(token);

        assertTrue(jwtUtils.validateToken(token));
        assertEquals("testuser", jwtUtils.extractUsername(token));
        assertEquals("PATIENT", jwtUtils.extractRole(token));
        assertEquals("test-id", jwtUtils.extractUserId(token));
    }
}
