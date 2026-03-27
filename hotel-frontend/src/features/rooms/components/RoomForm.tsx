import { NewRoomInput, Room } from '@/entities';

type RoomFormProps = {
  editingRoom: Room | null;
  newRoom: NewRoomInput;
  setEditingRoom: (room: Room | null) => void;
  setNewRoom: (room: NewRoomInput) => void;
  onAddRoom: (e: React.FormEvent) => void;
  onUpdateRoom: (e: React.FormEvent) => void;
};

export function RoomForm({
  editingRoom,
  newRoom,
  setEditingRoom,
  setNewRoom,
  onAddRoom,
  onUpdateRoom,
}: RoomFormProps) {
  return (
    <div style={{ marginBottom: '1.5rem' }}>
      <h3>{editingRoom ? 'Edit Room' : 'Add New Room'}</h3>
      <form onSubmit={editingRoom ? onUpdateRoom : onAddRoom} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        <input
          type="text"
          placeholder="Room Number"
          value={editingRoom ? editingRoom.number : newRoom.number}
          onChange={(e) =>
            editingRoom
              ? setEditingRoom({ ...editingRoom, number: e.target.value })
              : setNewRoom({ ...newRoom, number: e.target.value })
          }
          required
          style={{ padding: '0.5rem' }}
        />
        <select
          value={editingRoom ? editingRoom.typeId : newRoom.typeId}
          onChange={(e) =>
            editingRoom
              ? setEditingRoom({ ...editingRoom, typeId: e.target.value })
              : setNewRoom({ ...newRoom, typeId: e.target.value })
          }
          required
          style={{ padding: '0.5rem' }}
        >
          <option value="SINGLE">SINGLE</option>
          <option value="DOUBLE">DOUBLE</option>
          <option value="PREMIUM">PREMIUM</option>
        </select>
        <input
          type="text"
          placeholder="Name"
          value={editingRoom ? editingRoom.name : newRoom.name}
          onChange={(e) =>
            editingRoom
              ? setEditingRoom({ ...editingRoom, name: e.target.value })
              : setNewRoom({ ...newRoom, name: e.target.value })
          }
          required
          style={{ padding: '0.5rem' }}
        />
        <input
          type="number"
          placeholder="Floor"
          value={editingRoom ? editingRoom.floor : newRoom.floor}
          onChange={(e) =>
            editingRoom
              ? setEditingRoom({ ...editingRoom, floor: Number(e.target.value) })
              : setNewRoom({ ...newRoom, floor: Number(e.target.value) })
          }
          required
          style={{ padding: '0.5rem' }}
        />
        <label>
          <input
            type="checkbox"
            checked={editingRoom ? editingRoom.available : newRoom.available}
            onChange={(e) =>
              editingRoom
                ? setEditingRoom({ ...editingRoom, available: e.target.checked })
                : setNewRoom({ ...newRoom, available: e.target.checked })
            }
          />{' '}
          Available
        </label>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button type="submit" style={{ flex: 1 }}>
            {editingRoom ? 'Update Room' : 'Add Room'}
          </button>
          {editingRoom && (
            <button type="button" onClick={() => setEditingRoom(null)}>
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
}

