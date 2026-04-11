package edu.cit.dabon.campusguru.dto.booking;

import edu.cit.dabon.campusguru.enums.BookingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingResponse {
    private Long id;
    private Long listingId;
    private String subject;
    private String learnerId;
    private String learnerName;
    private String requestedTime;
    private String paymentType;
    private BookingStatus status;
    private String meetingLink;
    private String declineReason;
}
