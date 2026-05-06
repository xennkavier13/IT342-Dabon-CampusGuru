package edu.cit.dabon.campusguru.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.dabon.campusguru.auth.dto.AuthResponse;
import edu.cit.dabon.campusguru.auth.dto.LoginRequest;
import edu.cit.dabon.campusguru.auth.dto.RegisterRequest;
import edu.cit.dabon.campusguru.auth.security.JwtAuthenticationFilter;
import edu.cit.dabon.campusguru.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("testuser", "test@cit.edu",
                "password123", "Juan", "Dela Cruz", "LEARNER");
        loginRequest = new LoginRequest("testuser", "password123");
    }

    @Test
    void register_withValidData_returnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthResponse response = new AuthResponse(userId, "testuser", "test@cit.edu",
                "Juan", "Dela Cruz", "LEARNER", "jwt-token", "User registered successfully");

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_withDuplicateUsername_returnsBadRequest() throws Exception {
        AuthResponse response = new AuthResponse(null, null, null, null, null, null, null,
                "Username already exists");

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void login_withValidCredentials_returnsJwtToken() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthResponse response = new AuthResponse(userId, "testuser", "test@cit.edu",
                "Juan", "Dela Cruz", "LEARNER", "jwt-token", "Login successful");

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("LEARNER"));
    }

    @Test
    void login_withInvalidPassword_returns401Unauthorized() throws Exception {
        AuthResponse response = new AuthResponse(null, null, null, null, null, null, null,
                "Invalid username or password");

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
}
