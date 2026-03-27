'use client';

import { useEffect, useState } from 'react';
import {
  NewReservationInput,
  NewRoomInput,
  Reservation,
  Room,
} from '@/entities';
import { apiClient } from '@/shared/services/apiClient';
import { NotificationStack } from '@/shared/components/feedback/NotificationStack';
import { HotelsPanel } from '@/features/hotels/components/HotelsPanel';
import { useHotels } from '@/features/hotels/hooks/useHotels';
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

  const [rooms, setRooms] = useState<Room[]>([]);
  const [newRoom, setNewRoom] = useState<NewRoomInput>({ number: '', typeId: 'SINGLE', floor: 0, name: '', available: true });
  const [loadingRooms, setLoadingRooms] = useState(false);

  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [userId, setUserId] = useState('');
  const [newReservation, setNewReservation] = useState<NewReservationInput>({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '' });
  const [loadingReservations, setLoadingReservations] = useState(false);

  const [editingRoom, setEditingRoom] = useState<Room | null>(null);

  const handleError = async (response: Response, defaultMessage: string) => {
    const message = await parseErrorMessage(response, defaultMessage);
    showNotification(message, 'error');
  };

  useEffect(() => {
    if (selectedHotel) {
      fetchRooms(selectedHotel.id);
    } else {
      setRooms([]);
    }
  }, [selectedHotel]);

  // Room APIs
  const fetchRooms = async (hotelId: string) => {
    setLoadingRooms(true);
    try {
      const response = await apiClient.get(`/api/hotels/${hotelId}/rooms`);
      if (response.ok) {
        const data = await response.json();
        if (Array.isArray(data)) {
          const mappedData = data.map((room: any) => ({
            ...room,
            available: room.isAvailable ?? room.available
          }));
          setRooms(mappedData);
        } else {
          setRooms([]);
        }
      } else {
        await handleError(response, 'Failed to load rooms');
        setRooms([]);
      }
    } catch (error) {
      console.error('Failed to fetch rooms:', error);
      showNotification('Failed to fetch rooms', 'error');
      setRooms([]);
    } finally {
      setLoadingRooms(false);
    }
  };

  const handleAddRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedHotel) return;
    try {
      // Backend expects 'isAvailable', we map it from 'available'
      const roomToSubmit = {
        ...newRoom,
        isAvailable: newRoom.available
      };
      const response = await apiClient.post(`/api/hotels/${selectedHotel.id}/rooms`, roomToSubmit, {
        headers: { 'Content-Type': 'application/json' },
      });
      if (response.ok) {
        setNewRoom({ number: '', typeId: 'SINGLE', floor: 0, name: '', available: true });
        showNotification('Room added successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        await handleError(response, 'Failed to add room');
      }
    } catch (error) {
      console.error('Failed to add room:', error);
      showNotification('Error adding room', 'error');
    }
  };

  const handleUpdateRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingRoom || !selectedHotel) return;
    try {
      const roomToSubmit = {
        ...editingRoom,
        isAvailable: editingRoom.available
      };
      const response = await apiClient.put(`/api/hotels/${selectedHotel.id}/rooms/${editingRoom.id}`, roomToSubmit, {
        headers: { 'Content-Type': 'application/json' },
      });
      if (response.ok) {
        setEditingRoom(null);
        showNotification('Room updated successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        await handleError(response, 'Failed to update room');
      }
    } catch (error) {
      console.error('Failed to update room:', error);
      showNotification('Error updating room', 'error');
    }
  };

  const handleDeleteRoom = async (roomId: string) => {
    if (!selectedHotel) return;
    try {
      const response = await apiClient.delete(`/api/hotels/${selectedHotel.id}/rooms/${roomId}`);
      if (response.ok) {
        showNotification('Room deleted successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        await handleError(response, 'Failed to delete room');
      }
    } catch (error) {
      console.error('Failed to delete room:', error);
      showNotification('Error deleting room', 'error');
    }
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

        {/* ROOM MANAGEMENT */}
        <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px' }}>
          <h2>Rooms {selectedHotel ? `for ${selectedHotel.name}` : '(Select a hotel)'}</h2>
          
          {selectedHotel ? (
            <>
              <div style={{ marginBottom: '1.5rem' }}>
                <h3>{editingRoom ? 'Edit Room' : 'Add New Room'}</h3>
                <form onSubmit={editingRoom ? handleUpdateRoom : handleAddRoom} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                  <input 
                    type="text" 
                    placeholder="Room Number" 
                    value={editingRoom ? editingRoom.number : newRoom.number} 
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, number: e.target.value}) : setNewRoom({...newRoom, number: e.target.value})} 
                    required 
                    style={{ padding: '0.5rem' }} 
                  />
                  <select 
                    value={editingRoom ? editingRoom.typeId : newRoom.typeId} 
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, typeId: e.target.value}) : setNewRoom({...newRoom, typeId: e.target.value})} 
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
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, name: e.target.value}) : setNewRoom({...newRoom, name: e.target.value})} 
                    required 
                    style={{ padding: '0.5rem' }} 
                  />
                  <input 
                    type="number" 
                    placeholder="Floor" 
                    value={editingRoom ? editingRoom.floor : newRoom.floor} 
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, floor: Number(e.target.value)}) : setNewRoom({...newRoom, floor: Number(e.target.value)})} 
                    required 
                    style={{ padding: '0.5rem' }} 
                  />
                  <label>
                    <input 
                      type="checkbox" 
                      checked={editingRoom ? editingRoom.available : newRoom.available} 
                      onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, available: e.target.checked}) : setNewRoom({...newRoom, available: e.target.checked})} 
                    /> Available
                  </label>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button type="submit" style={{ flex: 1 }}>{editingRoom ? 'Update Room' : 'Add Room'}</button>
                    {editingRoom && <button type="button" onClick={() => setEditingRoom(null)}>Cancel</button>}
                  </div>
                </form>
              </div>

              <div>
                <h3>Rooms List</h3>
                {loadingRooms ? <p>Loading...</p> : (
                  <ul style={{ listStyle: 'none', padding: 0 }}>
                    {Array.isArray(rooms) && rooms.map(room => (
                      <li key={room.id} 
                        onClick={() => {
                          setNewReservation({ ...newReservation, roomId: room.id });
                          window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
                        }}
                        style={{ 
                          padding: '0.8rem', borderBottom: '1px solid #eee', display: 'flex', 
                          justifyContent: 'space-between', alignItems: 'center', 
                          cursor: 'pointer',
                          backgroundColor: newReservation.roomId === room.id ? '#f0fff4' : 'transparent',
                          transition: 'background-color 0.2s'
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = newReservation.roomId === room.id ? '#f0fff4' : '#f9f9f9')}
                        onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = newReservation.roomId === room.id ? '#f0fff4' : 'transparent')}
                      >
                        <div>
                          <strong>Room {room.number}</strong> ({room.typeId}) {room.name && `- ${room.name}`} <br/>
                          <small>Floor: {room.floor}</small> <br/>
                          <small style={{ color: room.available ? 'green' : 'red' }}>{room.available ? 'Available' : 'Booked'}</small> <br/>
                          <small style={{ color: '#666' }}>ID: {room.id}</small>
                        </div>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button onClick={(e) => {
                            e.stopPropagation();
                            setEditingRoom(room);
                          }}>Edit</button>
                          <button onClick={(e) => {
                            e.stopPropagation();
                            handleDeleteRoom(room.id);
                          }} style={{ color: 'red' }}>Del</button>
                        </div>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </>
          ) : <p>Select a hotel from the left to manage its rooms.</p>}
        </section>

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