package edu.cit.dabon.campusguru.feature.auth.register;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.cit.dabon.campusguru.auth.dto.RegisterUserRequest;
import edu.cit.dabon.campusguru.auth.dto.RegisterUserResponse;
import edu.cit.dabon.campusguru.auth.entity.User;
import edu.cit.dabon.campusguru.auth.repository.UserRepository;
import edu.cit.dabon.campusguru.auth.security.JwtService;
import edu.cit.dabon.campusguru.auth.service.AuthService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @SuppressWarnings("null")
    void handle_returnsCreatedResponseWhenUserIsNew() {
        RegisterUserRequest request = new RegisterUserRequest(
                "student1",
                "student1@campus.edu",
                "secret123",
                "Ana",
                "Reyes",
                null
        );

        when(userRepository.existsByUsername("student1")).thenReturn(false);
        when(userRepository.existsByInstitutionalEmail("student1@campus.edu")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(userRepository.save(org.mockito.ArgumentMatchers.<User>any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0, User.class);
            user.setUserId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        RegisterUserResponse response = authService.register(request);

        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), response.getUserId());
        assertEquals("student1", response.getUsername());
        assertEquals("student1@campus.edu", response.getInstitutionalEmail());
        assertEquals("Ana", response.getFirstName());
        assertEquals("Reyes", response.getLastName());
        assertEquals("LEARNER", response.getRole());
        assertEquals("jwt-token", response.getToken());
        assertEquals("User registered successfully", response.getMessage());
    }

    @Test
    void handle_returnsValidationResponseWhenUsernameExists() {
        RegisterUserRequest request = new RegisterUserRequest(
                "student1",
                "student1@campus.edu",
                "secret123",
                "Ana",
                "Reyes",
                null
        );

        when(userRepository.existsByUsername("student1")).thenReturn(true);

        RegisterUserResponse response = authService.register(request);

        assertNull(response.getUserId());
        assertEquals("Username already exists", response.getMessage());
    }
}