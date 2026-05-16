package edu.cit.dabon.campusguru.googleauth.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import edu.cit.dabon.campusguru.auth.model.User;
import edu.cit.dabon.campusguru.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${google.oauth.token-uri}")
    private String tokenUri;

    @Value("${google.oauth.auth-uri}")
    private String authUri;

    @Value("${google.oauth.scopes}")
    private String scopes;

    @Value("${google.oauth.frontend-redirect}")
    private String frontendRedirect;

    public GoogleAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Builds the Google OAuth consent URL that the tutor's browser will be redirected to.
     * Uses access_type=offline + prompt=consent to ensure we always get a refresh token.
     */
    public String buildAuthorizationUrl(UUID userId) {
        return authUri
                + "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=" + URLEncoder.encode(scopes.replace(",", " "), StandardCharsets.UTF_8)
                + "&access_type=offline"
                + "&prompt=consent"
                + "&state=" + userId.toString();
    }

    /**
     * Exchanges the authorization code for tokens and stores the refresh token on the user.
     * The 'state' parameter carries the userId so we know which tutor is completing the flow.
     */
    @SuppressWarnings("unchecked")
    public void handleCallback(String code, String state) {
        UUID userId = UUID.fromString(state);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            // Exchange authorization code for tokens via HTTP POST
            Map<String, String> params = new HashMap<>();
            params.put("code", code);
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("redirect_uri", redirectUri);
            params.put("grant_type", "authorization_code");

            HttpRequest request = GoogleNetHttpTransport.newTrustedTransport()
                    .createRequestFactory()
                    .buildPostRequest(new GenericUrl(tokenUri), new UrlEncodedContent(params));
            request.getHeaders().setAccept("application/json");

            HttpResponse response = request.execute();
            Map<String, Object> tokenResponse = JacksonFactory.getDefaultInstance()
                    .createJsonParser(response.getContent())
                    .parse(HashMap.class);

            String refreshToken = (String) tokenResponse.get("refresh_token");
            if (refreshToken != null) {
                user.setGoogleRefreshToken(refreshToken);
                userRepository.save(user);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No refresh token received. The user may need to revoke access and reconnect.");
            }
        } catch (IOException | GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to exchange authorization code: " + e.getMessage());
        }
    }

    /**
     * Gets a fresh access token by exchanging the stored refresh token.
     * Returns the access token string.
     */
    @SuppressWarnings("unchecked")
    public String getAccessToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getGoogleRefreshToken() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google Calendar not connected");
        }

        try {
            Map<String, String> params = new HashMap<>();
            params.put("refresh_token", user.getGoogleRefreshToken());
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("grant_type", "refresh_token");

            HttpRequest request = GoogleNetHttpTransport.newTrustedTransport()
                    .createRequestFactory()
                    .buildPostRequest(new GenericUrl(tokenUri), new UrlEncodedContent(params));
            request.getHeaders().setAccept("application/json");

            HttpResponse response = request.execute();
            Map<String, Object> tokenResponse = JacksonFactory.getDefaultInstance()
                    .createJsonParser(response.getContent())
                    .parse(HashMap.class);

            return (String) tokenResponse.get("access_token");
        } catch (IOException | GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to refresh access token: " + e.getMessage());
        }
    }

    /**
     * Returns an authorized Google Calendar API client for the given user.
     */
    public Calendar getCalendarService(UUID userId) {
        String accessToken = getAccessToken(userId);

        try {
            com.google.api.client.auth.oauth2.Credential credential =
                    new com.google.api.client.auth.oauth2.Credential(
                            com.google.api.client.auth.oauth2.BearerToken.authorizationHeaderAccessMethod())
                            .setAccessToken(accessToken);

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("CampusGuru")
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create Calendar service: " + e.getMessage());
        }
    }

    public boolean isConnected(UUID userId) {
        return userRepository.findById(userId)
                .map(User::isCalendarConnected)
                .orElse(false);
    }

    public String getFrontendRedirect() {
        return frontendRedirect;
    }
}
