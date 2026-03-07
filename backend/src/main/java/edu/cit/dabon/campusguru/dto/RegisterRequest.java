package edu.cit.dabon.campusguru.dto;

public class RegisterRequest {
    private String username;
    private String institutionalEmail;
    private String password;
    private String firstName;
    private String lastName;
    private String role; // LEARNER, TUTOR, ADMIN
    
    // Constructors
    public RegisterRequest() {
    }
    
    public RegisterRequest(String username, String institutionalEmail, String password, 
                          String firstName, String lastName, String role) {
        this.username = username;
        this.institutionalEmail = institutionalEmail;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    // Getters and Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
}
