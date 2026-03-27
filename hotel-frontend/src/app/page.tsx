'use client';

import { ReservationsPanel } from '@/features/reservations/components/ReservationsPanel';
import { NotificationStack } from '@/shared/components/feedback/NotificationStack';
import { HotelsPanel } from '@/features/hotels/components/HotelsPanel';
import { RoomsPanel } from '@/features/rooms/components/RoomsPanel';
import { useHotels } from '@/features/hotels/hooks/useHotels';
import { useRooms } from '@/features/rooms/hooks/useRooms';
import { useReservations } from '@/features/reservations/hooks/useReservations';
import { useNotifications } from '@/shared/hooks/useNotifications';

export default function Home() {
  const { notifications, showNotification, dismissNotification } = useNotifications();

  const {
    hotels,
    newHotel,
    setNewHotel,
    searchCity,
    setSearchCity,
    loadingHotels,
    selectedHotel,
    setSelectedHotel,
    editingHotel,
    setEditingHotel,
    handleSearchHotels,
    clearSearch,
    handleAddHotel,
    handleUpdateHotel,
    handleDeleteHotel,
  } = useHotels({ notify: showNotification });

  const {
    rooms,
    newRoom,
    setNewRoom,
    loadingRooms,
    editingRoom,
    setEditingRoom,
    handleAddRoom,
    handleUpdateRoom,
    handleDeleteRoom,
  } = useRooms({ selectedHotel, notify: showNotification });

  const {
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
  } = useReservations({ notify: showNotification });

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif', maxWidth: '1200px', margin: '0 auto' }}>
      <h1>Hotel Management System - API Testing Tool</h1>

      <NotificationStack notifications={notifications} onDismiss={dismissNotification} />

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>

        <HotelsPanel
          hotels={hotels}
          newHotel={newHotel}
          searchCity={searchCity}
          loadingHotels={loadingHotels}
          selectedHotel={selectedHotel}
          editingHotel={editingHotel}
          setNewHotel={setNewHotel}
          setSearchCity={setSearchCity}
          setSelectedHotel={setSelectedHotel}
          setEditingHotel={setEditingHotel}
          onSearchHotels={handleSearchHotels}
          onClearSearch={clearSearch}
          onAddHotel={handleAddHotel}
          onUpdateHotel={handleUpdateHotel}
          onDeleteHotel={handleDeleteHotel}
        />

        <RoomsPanel
          selectedHotel={selectedHotel}
          rooms={rooms}
          newRoom={newRoom}
          loadingRooms={loadingRooms}
          editingRoom={editingRoom}
          selectedReservationRoomId={newReservation.roomId}
          setNewRoom={setNewRoom}
          setEditingRoom={setEditingRoom}
          onAddRoom={handleAddRoom}
          onUpdateRoom={handleUpdateRoom}
          onDeleteRoom={handleDeleteRoom}
          onSelectRoomForReservation={(room) => {
            setReservationRoomId(room.id);
            window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
          }}
        />

        <ReservationsPanel
          newReservation={newReservation}
          setNewReservation={setNewReservation}
          reservations={reservations}
          userId={userId}
          setUserId={setUserId}
          loadingReservations={loadingReservations}
          onCreateReservation={handleCreateReservation}
          onSearchReservations={fetchUserReservations}
          onCancelReservation={handleCancelReservation}
        />

      </div>
    </div>
  );
}