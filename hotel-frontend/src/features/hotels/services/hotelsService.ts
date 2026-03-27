import { Hotel, NewHotelInput } from '@/entities';
import { apiClient } from '@/shared/services/apiClient';

export const hotelsService = {
  list() {
    return apiClient.get('/api/hotels', { cache: 'no-store' });
  },
  searchByCity(city: string) {
    return apiClient.get('/api/hotels/search', { query: { city }, cache: 'no-store' });
  },
  create(input: NewHotelInput) {
    return apiClient.post('/api/hotels', input, {
      headers: { 'Content-Type': 'application/json' },
    });
  },
  update(hotel: Hotel) {
    return apiClient.put(`/api/hotels/${hotel.id}`, hotel, {
      headers: { 'Content-Type': 'application/json' },
    });
  },
  remove(hotelId: string) {
    return apiClient.delete(`/api/hotels/${hotelId}`);
  },
};

