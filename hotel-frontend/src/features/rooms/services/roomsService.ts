import { Room } from '@/entities';
import { apiClient } from '@/shared/services/apiClient';

export const roomsService = {
  listByHotel(hotelId: string) {
    return apiClient.get(`/api/hotels/${hotelId}/rooms`);
  },
  create(hotelId: string, roomPayload: Record<string, unknown>) {
    return apiClient.post(`/api/hotels/${hotelId}/rooms`, roomPayload, {
      headers: { 'Content-Type': 'application/json' },
    });
  },
  update(hotelId: string, roomId: string, roomPayload: Record<string, unknown>) {
    return apiClient.put(`/api/hotels/${hotelId}/rooms/${roomId}`, roomPayload, {
      headers: { 'Content-Type': 'application/json' },
    });
  },
  remove(hotelId: string, roomId: string) {
    return apiClient.delete(`/api/hotels/${hotelId}/rooms/${roomId}`);
  },
};

