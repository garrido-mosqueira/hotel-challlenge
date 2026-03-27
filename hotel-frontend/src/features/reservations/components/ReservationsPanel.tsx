import { NewReservationInput, Reservation } from '@/entities';
import { ReservationForm } from '@/features/reservations/components/ReservationForm';
import { ReservationList } from '@/features/reservations/components/ReservationList';

type ReservationsPanelProps = {
  newReservation: NewReservationInput;
  setNewReservation: (value: NewReservationInput | ((prev: NewReservationInput) => NewReservationInput)) => void;
  reservations: Reservation[];
  userId: string;
  setUserId: (value: string) => void;
  loadingReservations: boolean;
  onCreateReservation: (e: React.FormEvent) => void;
  onSearchReservations: (e: React.FormEvent) => void;
  onCancelReservation: (id: string) => void;
};

export function ReservationsPanel({
  newReservation,
  setNewReservation,
  reservations,
  userId,
  setUserId,
  loadingReservations,
  onCreateReservation,
  onSearchReservations,
  onCancelReservation,
}: ReservationsPanelProps) {
  return (
    <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px', gridColumn: 'span 2' }}>
      <h2>Reservations</h2>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
        <ReservationForm
          newReservation={newReservation}
          setNewReservation={setNewReservation}
          onCreateReservation={onCreateReservation}
        />

        <ReservationList
          reservations={reservations}
          userId={userId}
          setUserId={setUserId}
          loadingReservations={loadingReservations}
          onSearchReservations={onSearchReservations}
          onCancelReservation={onCancelReservation}
        />
      </div>
    </section>
  );
}

