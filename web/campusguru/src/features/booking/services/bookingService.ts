import api from '@shared/services/api';
import type {
  Booking,
  CreateBookingRequest,
  UpdateBookingStatusRequest,
} from '../types/booking.types';

export const bookingService = {
  async createBooking(payload: CreateBookingRequest): Promise<Booking> {
    const response = await api.post<Booking>('/bookings', payload);
    return response.data;
  },

  async getPendingBookings(): Promise<Booking[]> {
    const response = await api.get<Booking[]>('/bookings/pending');
    return response.data;
  },

  async updateBookingStatus(id: number, payload: UpdateBookingStatusRequest): Promise<Booking> {
    const response = await api.put<Booking>(`/bookings/${id}/status`, payload);
    return response.data;
  },
};
