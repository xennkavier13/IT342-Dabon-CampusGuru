package edu.cit.dabon.campusguru.listing.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListingResponse {
    private Long id;
    private String tutorId;
    private String tutorName;
    private String subject;
    private String availableTime;
    private String contactInfo;
    private String proofOfCompetenceUrl;
}
