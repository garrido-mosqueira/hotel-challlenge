import { NewReservationInput } from '@/entities';

type ReservationFormProps = {
  newReservation: NewReservationInput;
  setNewReservation: (value: NewReservationInput | ((prev: NewReservationInput) => NewReservationInput)) => void;
  onCreateReservation: (e: React.FormEvent) => void;
};

export function ReservationForm({ newReservation, setNewReservation, onCreateReservation }: ReservationFormProps) {
  return (
    <div>
      <h3>Create Reservation</h3>
      <form onSubmit={onCreateReservation} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        <input
          type="text"
          placeholder="Guest ID"
          value={newReservation.guestId}
          onChange={(e) => setNewReservation({ ...newReservation, guestId: e.target.value })}
          required
          style={{ padding: '0.5rem' }}
        />
        <input
          type="text"
          placeholder="Room ID"
          value={newReservation.roomId}
          onChange={(e) => setNewReservation({ ...newReservation, roomId: e.target.value })}
          required
          style={{ padding: '0.5rem' }}
        />
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <div style={{ flex: 1 }}>
            <label>
              <small>Check-in</small>
            </label>
            <input
              type="date"
              value={newReservation.checkInDate}
              onChange={(e) => setNewReservation({ ...newReservation, checkInDate: e.target.value })}
              required
              style={{ padding: '0.5rem', width: '100%' }}
            />
          </div>
          <div style={{ flex: 1 }}>
            <label>
              <small>Check-out</small>
            </label>
            <input
              type="date"
              value={newReservation.checkOutDate}
              onChange={(e) => setNewReservation({ ...newReservation, checkOutDate: e.target.value })}
              required
              style={{ padding: '0.5rem', width: '100%' }}
            />
          </div>
        </div>
        <button type="submit">Create Reservation</button>
      </form>
    </div>
  );
}

