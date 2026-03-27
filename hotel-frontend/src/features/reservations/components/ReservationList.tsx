import { Reservation } from '@/entities';

type ReservationListProps = {
  reservations: Reservation[];
  userId: string;
  setUserId: (value: string) => void;
  loadingReservations: boolean;
  onSearchReservations: (e: React.FormEvent) => void;
  onCancelReservation: (id: string) => void;
};

export function ReservationList({
  reservations,
  userId,
  setUserId,
  loadingReservations,
  onSearchReservations,
  onCancelReservation,
}: ReservationListProps) {
  return (
    <div>
      <h3>Search Reservations by User</h3>
      <form onSubmit={onSearchReservations} style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
        <input
          type="text"
          placeholder="User ID"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
          required
          style={{ flex: 1, padding: '0.5rem' }}
        />
        <button type="submit">Search</button>
      </form>

      {loadingReservations ? (
        <p>Loading...</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {reservations.map((reservation) => (
            <li
              key={reservation.id}
              style={{ padding: '0.8rem', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
            >
              <div>
                <strong>{reservation.id}</strong> ({reservation.status}) <br />
                <small>
                  {reservation.checkInDate} to {reservation.checkOutDate}
                </small>{' '}
                <br />
                <small style={{ color: '#666' }}>Room: {reservation.roomName || reservation.roomId}</small>
              </div>
              <button onClick={() => onCancelReservation(reservation.id)} style={{ color: 'red' }}>
                Cancel
              </button>
            </li>
          ))}
          {reservations.length === 0 && userId && !loadingReservations && <p>No reservations found for this user.</p>}
        </ul>
      )}
    </div>
  );
}

