package edu.cit.dabon.campusguru.listing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.dabon.campusguru.auth.security.JwtAuthenticationFilter;
import edu.cit.dabon.campusguru.listing.dto.CreateListingRequest;
import edu.cit.dabon.campusguru.listing.dto.ListingResponse;
import edu.cit.dabon.campusguru.listing.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ListingService listingService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CreateListingRequest createRequest;
    private ListingResponse sampleResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateListingRequest();
        createRequest.setSubject("Computer Architecture");
        createRequest.setAvailableTime("Mon 3PM-5PM");
        createRequest.setContactInfo("fb: tutor.handle");
        createRequest.setProofOfCompetenceUrl("https://example.com/proof.pdf");

        sampleResponse = ListingResponse.builder()
                .id(1L)
                .tutorId("tutor-uuid")
                .tutorName("Juan Dela Cruz")
                .subject("Computer Architecture")
                .availableTime("Mon 3PM-5PM")
                .contactInfo("fb: tutor.handle")
                .proofOfCompetenceUrl("https://example.com/proof.pdf")
                .build();
    }

    @Test
    @WithMockUser
    void createListing_withValidData_returnsCreated() throws Exception {
        when(listingService.createListing(any(CreateListingRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.subject").value("Computer Architecture"));
    }

    @Test
    @WithMockUser
    void getAllListings_returnsListOfListings() throws Exception {
        when(listingService.getRecentListings()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Computer Architecture"))
                .andExpect(jsonPath("$[0].tutorName").value("Juan Dela Cruz"));
    }

    @Test
    @WithMockUser
    void getListingById_whenExists_returnsListing() throws Exception {
        when(listingService.getListingById(eq(1L))).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/listings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getListingById_whenNotFound_returns404() throws Exception {
        when(listingService.getListingById(eq(999L)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        mockMvc.perform(get("/api/listings/999"))
                .andExpect(status().isNotFound());
    }
}
