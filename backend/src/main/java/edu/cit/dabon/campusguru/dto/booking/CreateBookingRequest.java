package edu.cit.dabon.campusguru.dto.booking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequest {
    private Long listingId;
    private String requestedTime;
    private String paymentType;
}
