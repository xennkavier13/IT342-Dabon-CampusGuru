import api from '@shared/services/api';
import type { Listing, CreateListingRequest, SetAvailabilityRequest, AvailableSlot } from '../types/listing.types';

export const listingService = {
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

  async setAvailability(id: number, payload: SetAvailabilityRequest): Promise<Listing> {
    const response = await api.put<Listing>(`/listings/${id}/availability`, payload);
    return response.data;
  },

  async getAvailableSlots(id: number): Promise<AvailableSlot[]> {
    const response = await api.get<AvailableSlot[]>(`/listings/${id}/available-slots`);
    return response.data;
  },
};
