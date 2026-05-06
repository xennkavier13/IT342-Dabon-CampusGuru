package edu.cit.dabon.campusguru.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequest {
    private Long listingId;
    private String requestedTime;
    private String paymentType;
}
