import api from './api';
import type {
  Booking,
  CreateBookingRequest,
  CreateListingRequest,
  Listing,
  UpdateBookingStatusRequest,
} from '../types/marketplace.types';

export const marketplaceService = {
  async createListing(payload: CreateListingRequest): Promise<Listing> {
    const response = await api.post<Listing>('/listings', payload);
    return response.data;
  },

  async getListings(): Promise<Listing[]> {
    const response = await api.get<Listing[]>('/listings');
    return response.data;
  },

  async getListingById(id: number): Promise<Listing> {
    const response = await api.get<Listing>(`/listings/${id}`);
    return response.data;
  },

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
