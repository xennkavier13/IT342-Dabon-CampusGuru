package edu.cit.dabon.campusguru.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cit.dabon.campusguru.booking.model.BookingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingStatusRequest {
    private BookingStatus status;

    @JsonProperty("meeting_link")
    private String meetingLink;

    @JsonProperty("decline_reason")
    private String declineReason;
}
