package edu.cit.dabon.campusguru.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.dabon.campusguru.auth.security.JwtAuthenticationFilter;
import edu.cit.dabon.campusguru.booking.dto.BookingResponse;
import edu.cit.dabon.campusguru.booking.dto.CreateBookingRequest;
import edu.cit.dabon.campusguru.booking.dto.UpdateBookingStatusRequest;
import edu.cit.dabon.campusguru.booking.model.BookingStatus;
import edu.cit.dabon.campusguru.booking.service.BookingService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CreateBookingRequest createRequest;
    private BookingResponse sampleResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateBookingRequest();
        createRequest.setListingId(1L);
        createRequest.setRequestedTime("Tue 4PM-5PM");
        createRequest.setPaymentType("Cash");

        sampleResponse = BookingResponse.builder()
                .id(1L)
                .listingId(1L)
                .subject("Math")
                .learnerId("learner-uuid")
                .learnerName("Maria Santos")
                .requestedTime("Tue 4PM-5PM")
                .paymentType("Cash")
                .status(BookingStatus.PENDING)
                .build();
    }

    @Test
    @WithMockUser
    void createBooking_withValidData_returnsBooking() throws Exception {
        when(bookingService.createBooking(any(CreateBookingRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser
    void getPendingBookings_returnsListOfBookings() throws Exception {
        when(bookingService.getPendingBookingsForCurrentTutor()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/bookings/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].learnerName").value("Maria Santos"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser
    void updateBookingStatus_acceptWithMeetingLink_returnsAccepted() throws Exception {
        UpdateBookingStatusRequest updateRequest = new UpdateBookingStatusRequest();
        updateRequest.setStatus(BookingStatus.ACCEPTED);
        updateRequest.setMeetingLink("https://meet.google.com/abc");

        BookingResponse accepted = BookingResponse.builder()
                .id(1L).listingId(1L).subject("Math")
                .learnerId("learner-uuid").learnerName("Maria Santos")
                .requestedTime("Tue 4PM-5PM").paymentType("Cash")
                .status(BookingStatus.ACCEPTED).meetingLink("https://meet.google.com/abc")
                .build();

        when(bookingService.updateBookingStatus(eq(1L), any(UpdateBookingStatusRequest.class)))
                .thenReturn(accepted);

        mockMvc.perform(put("/api/bookings/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.meetingLink").value("https://meet.google.com/abc"));
    }

    @Test
    @WithMockUser
    void createBooking_whenListingNotFound_returns404() throws Exception {
        when(bookingService.createBooking(any(CreateBookingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }
}
