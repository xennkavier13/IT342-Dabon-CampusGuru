export interface Listing {
  id: number;
  tutorId: string;
  tutorName: string;
  subject: string;
  contactInfo: string;
  proofOfCompetenceUrl: string;
  availabilityStartDate?: string | null;
  availabilityEndDate?: string | null;
  availabilityDailyStartTime?: string | null;
  availabilityDailyEndTime?: string | null;
  calendarConnected?: boolean;
}

export interface CreateListingRequest {
  subject: string;
  contactInfo: string;
  proofOfCompetenceUrl: string;
}

export interface SetAvailabilityRequest {
  availabilityStartDate: string;
  availabilityEndDate: string;
  availabilityDailyStartTime: string;
  availabilityDailyEndTime: string;
}

export interface AvailableSlot {
  start: string;
  end: string;
}
