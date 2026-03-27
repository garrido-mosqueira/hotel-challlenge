import { Hotel } from '@/entities';

type HotelListProps = {
  hotels: Hotel[];
  loadingHotels: boolean;
  selectedHotelId?: string;
  onSelectHotel: (hotel: Hotel) => void;
  onEditHotel: (hotel: Hotel) => void;
  onDeleteHotel: (hotelId: string) => void;
};

export function HotelList({
  hotels,
  loadingHotels,
  selectedHotelId,
  onSelectHotel,
  onEditHotel,
  onDeleteHotel,
}: HotelListProps) {
  return (
    <div>
      <h3>Hotels List</h3>
      {loadingHotels ? (
        <p>Loading...</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {hotels.map(hotel => (
            <li
              key={hotel.id}
              style={{
                padding: '0.8rem',
                borderBottom: '1px solid #eee',
                backgroundColor: selectedHotelId === hotel.id ? '#f0f7ff' : 'transparent',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
            >
              <div onClick={() => onSelectHotel(hotel)} style={{ cursor: 'pointer', flex: 1 }}>
                <strong>{hotel.name}</strong> - {hotel.city} <br />
                <small style={{ color: '#666' }}>ID: {hotel.id}</small>
              </div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button onClick={() => onEditHotel(hotel)}>Edit</button>
                <button onClick={() => onDeleteHotel(hotel.id)} style={{ color: 'red' }}>
                  Del
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

