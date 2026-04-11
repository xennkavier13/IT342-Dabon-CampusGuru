package edu.cit.dabon.campusguru.dto.listing;

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
