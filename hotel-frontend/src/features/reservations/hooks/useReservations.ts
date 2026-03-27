import { FormEvent, useState } from 'react';
import { NewReservationInput, NotificationType, Reservation } from '@/entities';
import { parseErrorMessage } from '@/shared/services/errorParser';
import { reservationsService } from '@/features/reservations/services/reservationsService';

type UseReservationsOptions = {
  notify: (message: string, type?: NotificationType) => void;
};

const EMPTY_NEW_RESERVATION: NewReservationInput = {
  guestId: '',
  roomId: '',
  checkInDate: '',
  checkOutDate: '',
};

export function useReservations({ notify }: UseReservationsOptions) {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [userId, setUserId] = useState('');
  const [newReservation, setNewReservation] = useState<NewReservationInput>(EMPTY_NEW_RESERVATION);
  const [loadingReservations, setLoadingReservations] = useState(false);

  const handleError = async (response: Response, defaultMessage: string) => {
    const message = await parseErrorMessage(response, defaultMessage);
    notify(message, 'error');
  };

  const fetchUserReservations = async (e: FormEvent) => {
    e.preventDefault();
    setLoadingReservations(true);

    try {
      const response = await reservationsService.listByUser(userId);
      if (response.ok) {
        const data = await response.json();
        setReservations(Array.isArray(data) ? data : []);
        if (Array.isArray(data)) notify(`Found ${data.length} reservations`, 'info');
      } else {
        await handleError(response, 'Failed to load reservations');
        setReservations([]);
      }
    } catch (error) {
      console.error('Failed to fetch reservations:', error);
      notify('Error fetching reservations', 'error');
      setReservations([]);
    } finally {
      setLoadingReservations(false);
    }
  };

  const handleCreateReservation = async (e: FormEvent) => {
    e.preventDefault();

    try {
      const response = await reservationsService.create(newReservation);
      if (response.ok) {
        setNewReservation(EMPTY_NEW_RESERVATION);
        notify('Reservation created successfully!', 'success');
      } else {
        await handleError(response, 'Failed to create reservation');
      }
    } catch (error) {
      console.error('Failed to create reservation:', error);
      notify('Error creating reservation', 'error');
    }
  };

  const handleCancelReservation = async (id: string) => {
    try {
      const response = await reservationsService.remove(id);
      if (response.ok) {
        notify('Reservation cancelled successfully!', 'success');

        if (userId) {
          const refreshResponse = await reservationsService.listByUser(userId);
          const data = await refreshResponse.json();
          setReservations(Array.isArray(data) ? data : []);
        }
      } else {
        await handleError(response, 'Failed to cancel reservation');
      }
    } catch (error) {
      console.error('Failed to cancel reservation:', error);
      notify('Error cancelling reservation', 'error');
    }
  };

  const setReservationRoomId = (roomId: string) => {
    setNewReservation(prev => ({ ...prev, roomId }));
  };

  return {
    reservations,
    userId,
    setUserId,
    newReservation,
    setNewReservation,
    setReservationRoomId,
    loadingReservations,
    fetchUserReservations,
    handleCreateReservation,
    handleCancelReservation,
  };
}

