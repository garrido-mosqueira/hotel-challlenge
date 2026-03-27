import { Hotel, NewHotelInput } from '@/entities';
import { HotelForm } from '@/features/hotels/components/HotelForm';
import { HotelList } from '@/features/hotels/components/HotelList';

type HotelsPanelProps = {
  hotels: Hotel[];
  newHotel: NewHotelInput;
  searchCity: string;
  loadingHotels: boolean;
  selectedHotel: Hotel | null;
  editingHotel: Hotel | null;
  setNewHotel: (hotel: NewHotelInput) => void;
  setSearchCity: (city: string) => void;
  setSelectedHotel: (hotel: Hotel | null) => void;
  setEditingHotel: (hotel: Hotel | null) => void;
  onSearchHotels: (e: React.FormEvent) => void;
  onClearSearch: () => void;
  onAddHotel: (e: React.FormEvent) => void;
  onUpdateHotel: (e: React.FormEvent) => void;
  onDeleteHotel: (hotelId: string) => void;
};

export function HotelsPanel({
  hotels,
  newHotel,
  searchCity,
  loadingHotels,
  selectedHotel,
  editingHotel,
  setNewHotel,
  setSearchCity,
  setSelectedHotel,
  setEditingHotel,
  onSearchHotels,
  onClearSearch,
  onAddHotel,
  onUpdateHotel,
  onDeleteHotel,
}: HotelsPanelProps) {
  return (
    <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px' }}>
      <h2>Hotels</h2>

      <div style={{ marginBottom: '1.5rem' }}>
        <h3>Search Hotels</h3>
        <form onSubmit={onSearchHotels} style={{ display: 'flex', gap: '0.5rem' }}>
          <input
            type="text"
            placeholder="City"
            value={searchCity}
            onChange={(e) => setSearchCity(e.target.value)}
            style={{ flex: 1, padding: '0.5rem' }}
          />
          <button type="submit">Search</button>
          <button type="button" onClick={onClearSearch}>
            Clear
          </button>
        </form>
      </div>

      <HotelForm
        editingHotel={editingHotel}
        newHotel={newHotel}
        setEditingHotel={setEditingHotel}
        setNewHotel={setNewHotel}
        onAddHotel={onAddHotel}
        onUpdateHotel={onUpdateHotel}
      />

      <HotelList
        hotels={hotels}
        loadingHotels={loadingHotels}
        selectedHotelId={selectedHotel?.id}
        onSelectHotel={setSelectedHotel}
        onEditHotel={setEditingHotel}
        onDeleteHotel={onDeleteHotel}
      />
    </section>
  );
}

