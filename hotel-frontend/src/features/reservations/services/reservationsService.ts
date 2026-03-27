import { NewReservationInput } from '@/entities';
import { apiClient } from '@/shared/services/apiClient';

export const reservationsService = {
  listByUser(userId: string) {
    return apiClient.get('/api/reservations', {
      headers: { 'X-User-Id': userId },
    });
  },
  create(input: NewReservationInput) {
    return apiClient.post('/api/reservations', input, {
      headers: { 'Content-Type': 'application/json' },
    });
  },
  remove(id: string) {
    return apiClient.delete(`/api/reservations/${id}`);
  },
};

