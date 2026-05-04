package edu.cit.dabon.campusguru.bookings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.cit.dabon.campusguru.bookings.enums.BookingStatus;

public class UpdateBookingStatusRequest {
    private BookingStatus status;

    @JsonProperty("meeting_link")
    private String meetingLink;

    @JsonProperty("decline_reason")
    private String declineReason;

    public UpdateBookingStatusRequest() {
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }
}
