package edu.cit.dabon.campusguru.service;

import edu.cit.dabon.campusguru.dto.AuthResponse;
import edu.cit.dabon.campusguru.dto.LoginRequest;
import edu.cit.dabon.campusguru.dto.RegisterRequest;
import edu.cit.dabon.campusguru.entity.User;
import edu.cit.dabon.campusguru.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse(null, null, null, null, null, null, 
                "Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByInstitutionalEmail(request.getInstitutionalEmail())) {
            return new AuthResponse(null, null, null, null, null, null, 
                "Email already exists");
        }
        
        // Create new user
        User user = new User(
            request.getUsername(),
            request.getInstitutionalEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName(),
            request.getRole() != null ? request.getRole() : "LEARNER"
        );
        
        User savedUser = userRepository.save(user);
        
        return new AuthResponse(
            savedUser.getUserId(),
            savedUser.getUsername(),
            savedUser.getInstitutionalEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getRole(),
            "User registered successfully"
        );
    }
    
    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        
        if (userOptional.isEmpty()) {
            return new AuthResponse(null, null, null, null, null, null, 
                "Invalid username or password");
        }
        
        User user = userOptional.get();
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return new AuthResponse(null, null, null, null, null, null, 
                "Invalid username or password");
        }
        
        return new AuthResponse(
            user.getUserId(),
            user.getUsername(),
            user.getInstitutionalEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            "Login successful"
        );
    }
}
