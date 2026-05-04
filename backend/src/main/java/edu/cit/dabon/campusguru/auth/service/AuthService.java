package edu.cit.dabon.campusguru.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.dabon.campusguru.auth.dto.LoginUserRequest;
import edu.cit.dabon.campusguru.auth.dto.LoginUserResponse;
import edu.cit.dabon.campusguru.auth.dto.RegisterUserRequest;
import edu.cit.dabon.campusguru.auth.dto.RegisterUserResponse;
import edu.cit.dabon.campusguru.auth.entity.User;
import edu.cit.dabon.campusguru.auth.repository.UserRepository;
import edu.cit.dabon.campusguru.auth.security.JwtService;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new RegisterUserResponse(null, null, null, null, null, null, null,
                    "Username already exists");
        }

        if (userRepository.existsByInstitutionalEmail(request.getInstitutionalEmail())) {
            return new RegisterUserResponse(null, null, null, null, null, null, null,
                    "Email already exists");
        }

        User user = new User(
                request.getUsername(),
                request.getInstitutionalEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getRole() != null ? request.getRole() : "LEARNER"
        );

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new RegisterUserResponse(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getInstitutionalEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole(),
                token,
                "User registered successfully"
        );
    }

    @Transactional(readOnly = true)
    public LoginUserResponse login(LoginUserRequest request) {
        String loginIdentifier = request.getUsername() != null
                ? request.getUsername().trim().toLowerCase()
                : "";

        Optional<User> userOptional = userRepository.findByUsername(loginIdentifier);

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByInstitutionalEmail(loginIdentifier);
        }

        if (userOptional.isEmpty()) {
            return new LoginUserResponse(null, null, null, null, null, null, null,
                    "Invalid username or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return new LoginUserResponse(null, null, null, null, null, null, null,
                    "Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginUserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getInstitutionalEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                token,
                "Login successful"
        );
    }
}
