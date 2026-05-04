package edu.cit.dabon.campusguru.auth.dto;

import java.util.UUID;

public class RegisterUserResponse {
    private UUID userId;
    private String username;
    private String institutionalEmail;
    private String firstName;
    private String lastName;
    private String role;
    private String token;
    private String message;

    public RegisterUserResponse() {
    }

    public RegisterUserResponse(UUID userId, String username, String institutionalEmail,
                                String firstName, String lastName, String role, String token,
                                String message) {
        this.userId = userId;
        this.username = username;
        this.institutionalEmail = institutionalEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.token = token;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
