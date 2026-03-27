import { Hotel, NewRoomInput, Room } from '@/entities';
import { RoomForm } from '@/features/rooms/components/RoomForm';
import { RoomList } from '@/features/rooms/components/RoomList';

type RoomsPanelProps = {
  selectedHotel: Hotel | null;
  rooms: Room[];
  newRoom: NewRoomInput;
  loadingRooms: boolean;
  editingRoom: Room | null;
  selectedReservationRoomId: string;
  setNewRoom: (room: NewRoomInput) => void;
  setEditingRoom: (room: Room | null) => void;
  onAddRoom: (e: React.FormEvent) => void;
  onUpdateRoom: (e: React.FormEvent) => void;
  onDeleteRoom: (roomId: string) => void;
  onSelectRoomForReservation: (room: Room) => void;
};

export function RoomsPanel({
  selectedHotel,
  rooms,
  newRoom,
  loadingRooms,
  editingRoom,
  selectedReservationRoomId,
  setNewRoom,
  setEditingRoom,
  onAddRoom,
  onUpdateRoom,
  onDeleteRoom,
  onSelectRoomForReservation,
}: RoomsPanelProps) {
  return (
    <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px' }}>
      <h2>Rooms {selectedHotel ? `for ${selectedHotel.name}` : '(Select a hotel)'}</h2>

      {selectedHotel ? (
        <>
          <RoomForm
            editingRoom={editingRoom}
            newRoom={newRoom}
            setEditingRoom={setEditingRoom}
            setNewRoom={setNewRoom}
            onAddRoom={onAddRoom}
            onUpdateRoom={onUpdateRoom}
          />

          <RoomList
            rooms={rooms}
            loadingRooms={loadingRooms}
            selectedRoomId={selectedReservationRoomId}
            onSelectRoom={onSelectRoomForReservation}
            onEditRoom={setEditingRoom}
            onDeleteRoom={onDeleteRoom}
          />
        </>
      ) : (
        <p>Select a hotel from the left to manage its rooms.</p>
      )}
    </section>
  );
}

