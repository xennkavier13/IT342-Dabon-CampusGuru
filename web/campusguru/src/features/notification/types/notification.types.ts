export type NotificationType = 'BOOKING_RECEIVED' | 'BOOKING_ACCEPTED' | 'BOOKING_DECLINED';

export interface Notification {
  id: string;
  type: NotificationType;
  bookingId: number;
  message: string;
  isRead: boolean;
  createdAt: string;
}
