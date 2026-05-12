package edu.cit.dabon.campusguru.listing.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class SetAvailabilityRequest {
    private LocalDate availabilityStartDate;
    private LocalDate availabilityEndDate;
    private LocalTime availabilityDailyStartTime;
    private LocalTime availabilityDailyEndTime;
}
