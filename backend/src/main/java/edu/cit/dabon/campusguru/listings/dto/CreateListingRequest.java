package edu.cit.dabon.campusguru.listings.dto;

public class CreateListingRequest {
    private String subject;
    private String availableTime;
    private String contactInfo;
    private String proofOfCompetenceUrl;

    public CreateListingRequest() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getProofOfCompetenceUrl() {
        return proofOfCompetenceUrl;
    }

    public void setProofOfCompetenceUrl(String proofOfCompetenceUrl) {
        this.proofOfCompetenceUrl = proofOfCompetenceUrl;
    }
}
