import { Hotel, NewHotelInput } from '@/entities';

type HotelFormProps = {
  editingHotel: Hotel | null;
  newHotel: NewHotelInput;
  setEditingHotel: (hotel: Hotel | null) => void;
  setNewHotel: (hotel: NewHotelInput) => void;
  onAddHotel: (e: React.FormEvent) => void;
  onUpdateHotel: (e: React.FormEvent) => void;
};

export function HotelForm({
  editingHotel,
  newHotel,
  setEditingHotel,
  setNewHotel,
  onAddHotel,
  onUpdateHotel,
}: HotelFormProps) {
  return (
    <div style={{ marginBottom: '1.5rem' }}>
      <h3>{editingHotel ? 'Edit Hotel' : 'Add New Hotel'}</h3>
      <form onSubmit={editingHotel ? onUpdateHotel : onAddHotel} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        <input
          type="text"
          placeholder="Name"
          value={editingHotel ? editingHotel.name : newHotel.name}
          onChange={(e) =>
            editingHotel
              ? setEditingHotel({ ...editingHotel, name: e.target.value })
              : setNewHotel({ ...newHotel, name: e.target.value })
          }
          required
          style={{ padding: '0.5rem' }}
        />
        <input
          type="text"
          placeholder="City"
          value={editingHotel ? editingHotel.city : newHotel.city}
          onChange={(e) =>
            editingHotel
              ? setEditingHotel({ ...editingHotel, city: e.target.value })
              : setNewHotel({ ...newHotel, city: e.target.value })
          }
          required
          style={{ padding: '0.5rem' }}
        />
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button type="submit" style={{ flex: 1 }}>
            {editingHotel ? 'Update Hotel' : 'Add Hotel'}
          </button>
          {editingHotel && (
            <button type="button" onClick={() => setEditingHotel(null)}>
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
}

