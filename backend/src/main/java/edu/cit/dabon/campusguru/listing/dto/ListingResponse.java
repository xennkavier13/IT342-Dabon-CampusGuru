package edu.cit.dabon.campusguru.listing.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ListingResponse {
    private Long id;
    private String tutorId;
    private String tutorName;
    private String subject;
    private String contactInfo;
    private String proofOfCompetenceUrl;
    
    // Google Calendar availability window
    private LocalDate availabilityStartDate;
    private LocalDate availabilityEndDate;
    private LocalTime availabilityDailyStartTime;
    private LocalTime availabilityDailyEndTime;
    private boolean calendarConnected;
}
