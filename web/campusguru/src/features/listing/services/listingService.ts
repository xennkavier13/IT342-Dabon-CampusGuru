import api from '@shared/services/api';
import type { Listing, CreateListingRequest } from '../types/listing.types';

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
};
