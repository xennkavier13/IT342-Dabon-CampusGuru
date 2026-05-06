package edu.cit.dabon.campusguru.listing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateListingRequest {
    private String subject;
    private String availableTime;
    private String contactInfo;
    private String proofOfCompetenceUrl;
}
