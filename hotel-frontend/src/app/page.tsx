'use client';

import { useEffect, useState } from 'react';
import {
  NewReservationInput,
  Reservation,
} from '@/entities';
import { apiClient } from '@/shared/services/apiClient';
import { NotificationStack } from '@/shared/components/feedback/NotificationStack';
import { HotelsPanel } from '@/features/hotels/components/HotelsPanel';
import { RoomsPanel } from '@/features/rooms/components/RoomsPanel';
import { useHotels } from '@/features/hotels/hooks/useHotels';
import { useRooms } from '@/features/rooms/hooks/useRooms';
import { useNotifications } from '@/shared/hooks/useNotifications';
import { parseErrorMessage } from '@/shared/services/errorParser';

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

  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [userId, setUserId] = useState('');
  const [newReservation, setNewReservation] = useState<NewReservationInput>({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '' });
  const [loadingReservations, setLoadingReservations] = useState(false);

  const handleError = async (response: Response, defaultMessage: string) => {
    const message = await parseErrorMessage(response, defaultMessage);
    showNotification(message, 'error');
  };

  // Reservation APIs
  const fetchUserReservations = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoadingReservations(true);
    try {
      const response = await apiClient.get('/api/reservations', {
        headers: { 'X-User-Id': userId }
      });
      if (response.ok) {
        const data = await response.json();
        setReservations(Array.isArray(data) ? data : []);
        if (Array.isArray(data)) showNotification(`Found ${data.length} reservations`, 'info');
      } else {
        await handleError(response, 'Failed to load reservations');
        setReservations([]);
      }
    } catch (error) {
      console.error('Failed to fetch reservations:', error);
      showNotification('Error fetching reservations', 'error');
      setReservations([]);
    } finally {
      setLoadingReservations(false);
    }
  };

  const handleCreateReservation = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await apiClient.post('/api/reservations', newReservation, {
        headers: { 'Content-Type': 'application/json' },
      });
      if (response.ok) {
        setNewReservation({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '' });
        showNotification('Reservation created successfully!', 'success');
      } else {
        await handleError(response, 'Failed to create reservation');
      }
    } catch (error) {
      console.error('Failed to create reservation:', error);
      showNotification('Error creating reservation', 'error');
    }
  };

  const handleCancelReservation = async (id: string) => {
    try {
      const response = await apiClient.delete(`/api/reservations/${id}`);
      if (response.ok) {
        showNotification('Reservation cancelled successfully!', 'success');
        if (userId) {
          // Refresh if user ID is entered
          const refreshResponse = await apiClient.get('/api/reservations', { headers: { 'X-User-Id': userId } });
          const data = await refreshResponse.json();
          if (Array.isArray(data)) {
            setReservations(data);
          } else {
            setReservations([]);
          }
        }
      } else {
        await handleError(response, 'Failed to cancel reservation');
      }
    } catch (error) {
      console.error('Failed to cancel reservation:', error);
      showNotification('Error cancelling reservation', 'error');
    }
  };

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
            setNewReservation({ ...newReservation, roomId: room.id });
            window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
          }}
        />

        {/* RESERVATION MANAGEMENT */}
        <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px', gridColumn: 'span 2' }}>
          <h2>Reservations</h2>
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
            <div>
              <h3>Create Reservation</h3>
              <form onSubmit={handleCreateReservation} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                <input 
                  type="text" 
                  placeholder="Guest ID" 
                  value={newReservation.guestId} 
                  onChange={(e) => setNewReservation({...newReservation, guestId: e.target.value})} 
                  required 
                  style={{ padding: '0.5rem' }} 
                />
                <input 
                  type="text" 
                  placeholder="Room ID" 
                  value={newReservation.roomId} 
                  onChange={(e) => setNewReservation({...newReservation, roomId: e.target.value})} 
                  required 
                  style={{ padding: '0.5rem' }} 
                />
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <div style={{ flex: 1 }}>
                    <label><small>Check-in</small></label>
                    <input 
                      type="date" 
                      value={newReservation.checkInDate} 
                      onChange={(e) => setNewReservation({...newReservation, checkInDate: e.target.value})} 
                      required 
                      style={{ padding: '0.5rem', width: '100%' }} 
                    />
                  </div>
                  <div style={{ flex: 1 }}>
                    <label><small>Check-out</small></label>
                    <input 
                      type="date" 
                      value={newReservation.checkOutDate} 
                      onChange={(e) => setNewReservation({...newReservation, checkOutDate: e.target.value})} 
                      required 
                      style={{ padding: '0.5rem', width: '100%' }} 
                    />
                  </div>
                </div>
                <button type="submit">Create Reservation</button>
              </form>
            </div>

            <div>
              <h3>Search Reservations by User</h3>
              <form onSubmit={fetchUserReservations} style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
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

              {loadingReservations ? <p>Loading...</p> : (
                <ul style={{ listStyle: 'none', padding: 0 }}>
                  {Array.isArray(reservations) && reservations.map(res => (
                    <li key={res.id} style={{ padding: '0.8rem', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div>
                        <strong>{res.id}</strong> ({res.status}) <br/>
                        <small>{res.checkInDate} to {res.checkOutDate}</small> <br/>
                        <small style={{ color: '#666' }}>Room: {res.roomName || res.roomId}</small>
                      </div>
                      <button onClick={() => handleCancelReservation(res.id)} style={{ color: 'red' }}>Cancel</button>
                    </li>
                  ))}
                  {reservations.length === 0 && userId && !loadingReservations && <p>No reservations found for this user.</p>}
                </ul>
              )}
            </div>
          </div>
        </section>

      </div>
    </div>
  );
}