export interface Listing {
  id: number;
  tutorId: string;
  tutorName: string;
  subject: string;
  availableTime: string;
  contactInfo: string;
  proofOfCompetenceUrl: string;
}

export interface CreateListingRequest {
  subject: string;
  availableTime: string;
  contactInfo: string;
  proofOfCompetenceUrl: string;
}
