package edu.cit.dabon.campusguru.listing.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AvailableSlot {
    private LocalDateTime start;
    private LocalDateTime end;
}
