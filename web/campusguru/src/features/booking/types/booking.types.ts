export type BookingStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED';

export interface Booking {
  id: number;
  listingId: number;
  subject: string;
  learnerId: string;
  learnerName: string;
  requestedTime: string;
  paymentType: string;
  status: BookingStatus;
  meetingLink?: string | null;
  declineReason?: string | null;
}

export interface CreateBookingRequest {
  listingId: number;
  requestedTime: string;
  paymentType: string;
}

export interface UpdateBookingStatusRequest {
  status: BookingStatus;
  meeting_link?: string;
  decline_reason?: string;
}
