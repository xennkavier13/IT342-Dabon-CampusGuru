package edu.cit.dabon.campusguru.googleauth.controller;

import edu.cit.dabon.campusguru.auth.model.User;
import edu.cit.dabon.campusguru.auth.service.AuthenticatedUserService;
import edu.cit.dabon.campusguru.googleauth.service.GoogleAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/google-auth")
@CrossOrigin(origins = "*")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final AuthenticatedUserService authenticatedUserService;

    public GoogleAuthController(GoogleAuthService googleAuthService,
                                 AuthenticatedUserService authenticatedUserService) {
        this.googleAuthService = googleAuthService;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Initiates the Google OAuth flow.
     * The tutor's browser is redirected to Google's consent screen.
     */
    @GetMapping("/connect")
    public void connect(HttpServletResponse response) throws IOException {
        User currentUser = authenticatedUserService.getCurrentUser();
        String authUrl = googleAuthService.buildAuthorizationUrl(currentUser.getUserId());
        response.sendRedirect(authUrl);
    }

    /**
     * Handles the OAuth redirect from Google.
     * This endpoint is called by Google after the tutor grants consent.
     * It must be permitAll() in SecurityConfig since it's a redirect from Google (no JWT).
     */
    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                          @RequestParam("state") String state,
                          HttpServletResponse response) throws IOException {
        googleAuthService.handleCallback(code, state);
        // Redirect the tutor's browser back to the frontend dashboard
        response.sendRedirect(googleAuthService.getFrontendRedirect() + "?google_connected=true");
    }

    /**
     * Returns the calendar connection status for the current user.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus() {
        User currentUser = authenticatedUserService.getCurrentUser();
        boolean connected = googleAuthService.isConnected(currentUser.getUserId());
        return ResponseEntity.ok(Map.of("connected", connected));
    }
}
