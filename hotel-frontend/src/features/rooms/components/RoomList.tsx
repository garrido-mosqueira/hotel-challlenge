import { Room } from '@/entities';

type RoomListProps = {
  rooms: Room[];
  loadingRooms: boolean;
  selectedRoomId: string;
  onSelectRoom: (room: Room) => void;
  onEditRoom: (room: Room) => void;
  onDeleteRoom: (roomId: string) => void;
};

export function RoomList({
  rooms,
  loadingRooms,
  selectedRoomId,
  onSelectRoom,
  onEditRoom,
  onDeleteRoom,
}: RoomListProps) {
  return (
    <div>
      <h3>Rooms List</h3>
      {loadingRooms ? (
        <p>Loading...</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {rooms.map(room => (
            <li
              key={room.id}
              onClick={() => onSelectRoom(room)}
              style={{
                padding: '0.8rem',
                borderBottom: '1px solid #eee',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                cursor: 'pointer',
                backgroundColor: selectedRoomId === room.id ? '#f0fff4' : 'transparent',
                transition: 'background-color 0.2s',
              }}
              onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = selectedRoomId === room.id ? '#f0fff4' : '#f9f9f9')}
              onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = selectedRoomId === room.id ? '#f0fff4' : 'transparent')}
            >
              <div>
                <strong>Room {room.number}</strong> ({room.typeId}) {room.name && `- ${room.name}`} <br />
                <small>Floor: {room.floor}</small> <br />
                <small style={{ color: room.available ? 'green' : 'red' }}>{room.available ? 'Available' : 'Booked'}</small> <br />
                <small style={{ color: '#666' }}>ID: {room.id}</small>
              </div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onEditRoom(room);
                  }}
                >
                  Edit
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onDeleteRoom(room.id);
                  }}
                  style={{ color: 'red' }}
                >
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

