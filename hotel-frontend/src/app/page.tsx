'use client';

import { useEffect, useState } from 'react';

type Hotel = {
  id: string;
  name: string;
  city: string;
};

type Room = {
  id: string;
  hotelId: string;
  number: string;
  typeId: string;
  floor: number;
  name: string;
  available: boolean;
};

type Reservation = {
  id: string;
  guestId: string;
  roomId: string;
  roomName: string;
  checkInDate: string;
  checkOutDate: string;
  status: string;
};

type Notification = {
  message: string;
  type: 'success' | 'error' | 'info';
  id: number;
};

export default function Home() {
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [newHotel, setNewHotel] = useState({ name: '', city: '' });
  const [searchCity, setSearchCity] = useState('');
  const [loadingHotels, setLoadingHotels] = useState(true);

  const [selectedHotel, setSelectedHotel] = useState<Hotel | null>(null);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [newRoom, setNewRoom] = useState({ number: '', typeId: '', floor: 0, name: '', available: true });
  const [loadingRooms, setLoadingRooms] = useState(false);

  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [userId, setUserId] = useState('');
  const [newReservation, setNewReservation] = useState({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '' });
  const [loadingReservations, setLoadingReservations] = useState(false);

  const [editingHotel, setEditingHotel] = useState<Hotel | null>(null);
  const [editingRoom, setEditingRoom] = useState<Room | null>(null);

  const [notifications, setNotifications] = useState<Notification[]>([]);

  const showNotification = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
    const id = Date.now();
    setNotifications(prev => [...prev, { message, type, id }]);
    setTimeout(() => {
      setNotifications(prev => prev.filter(n => n.id !== id));
    }, 5000);
  };

  useEffect(() => {
    fetchHotels();
  }, []);

  useEffect(() => {
    if (selectedHotel) {
      fetchRooms(selectedHotel.id);
    } else {
      setRooms([]);
    }
  }, [selectedHotel]);

  // Hotel APIs
  const fetchHotels = async () => {
    setLoadingHotels(true);
    try {
      // Use no-cache to ensure we get fresh data from the API
      const response = await fetch('/api/hotels', { cache: 'no-store' });
      const data = await response.json();
      if (Array.isArray(data)) {
        setHotels(data);
      } else {
        console.error('Expected hotels array but got:', data);
        showNotification('Failed to load hotels: unexpected data format', 'error');
        setHotels([]);
      }
    } catch (error) {
      console.error('Failed to fetch hotels:', error);
      showNotification('Failed to fetch hotels', 'error');
      setHotels([]);
    } finally {
      setLoadingHotels(false);
    }
  };

  const handleSearchHotels = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchCity) {
      fetchHotels();
      return;
    }
    setLoadingHotels(true);
    try {
      const response = await fetch(`/api/hotels/search?city=${searchCity}`, { cache: 'no-store' });
      const data = await response.json();
      if (Array.isArray(data)) {
        setHotels(data);
        showNotification(`Found ${data.length} hotels`, 'info');
      } else {
        console.error('Expected hotels array but got:', data);
        showNotification('Search failed: unexpected data format', 'error');
        setHotels([]);
      }
    } catch (error) {
      console.error('Failed to search hotels:', error);
      showNotification('Failed to search hotels', 'error');
      setHotels([]);
    } finally {
      setLoadingHotels(false);
    }
  };

  const handleAddHotel = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/hotels', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newHotel),
      });
      if (response.ok) {
        setNewHotel({ name: '', city: '' });
        showNotification('Hotel added successfully!', 'success');
        fetchHotels();
      } else {
        showNotification('Failed to add hotel', 'error');
      }
    } catch (error) {
      console.error('Failed to add hotel:', error);
      showNotification('Error adding hotel', 'error');
    }
  };

  const handleUpdateHotel = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingHotel) return;
    try {
      const response = await fetch(`/api/hotels/${editingHotel.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(editingHotel),
      });
      if (response.ok) {
        setEditingHotel(null);
        showNotification('Hotel updated successfully!', 'success');
        fetchHotels();
      } else {
        showNotification('Failed to update hotel', 'error');
      }
    } catch (error) {
      console.error('Failed to update hotel:', error);
      showNotification('Error updating hotel', 'error');
    }
  };

  const handleDeleteHotel = async (hotelId: string) => {
    try {
      const response = await fetch(`/api/hotels/${hotelId}`, { method: 'DELETE' });
      if (response.ok) {
        if (selectedHotel?.id === hotelId) setSelectedHotel(null);
        showNotification('Hotel deleted successfully!', 'success');
        fetchHotels();
      } else {
        showNotification('Failed to delete hotel', 'error');
      }
    } catch (error) {
      console.error('Failed to delete hotel:', error);
      showNotification('Error deleting hotel', 'error');
    }
  };

  // Room APIs
  const fetchRooms = async (hotelId: string) => {
    setLoadingRooms(true);
    try {
      const response = await fetch(`/api/hotels/${hotelId}/rooms`);
      const data = await response.json();
      if (Array.isArray(data)) {
        // Map backend's 'isAvailable' to frontend's 'available'
        const mappedData = data.map((room: any) => ({
          ...room,
          available: room.isAvailable ?? room.available
        }));
        setRooms(mappedData);
      } else {
        console.error('Expected rooms array but got:', data);
        showNotification('Failed to load rooms: unexpected data format', 'error');
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
      const response = await fetch(`/api/hotels/${selectedHotel.id}/rooms`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(roomToSubmit),
      });
      if (response.ok) {
        setNewRoom({ number: '', typeId: '', floor: 0, name: '', available: true });
        showNotification('Room added successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        showNotification('Failed to add room', 'error');
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
      const response = await fetch(`/api/hotels/${selectedHotel.id}/rooms/${editingRoom.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(roomToSubmit),
      });
      if (response.ok) {
        setEditingRoom(null);
        showNotification('Room updated successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        showNotification('Failed to update room', 'error');
      }
    } catch (error) {
      console.error('Failed to update room:', error);
      showNotification('Error updating room', 'error');
    }
  };

  const handleDeleteRoom = async (roomId: string) => {
    if (!selectedHotel) return;
    try {
      const response = await fetch(`/api/hotels/${selectedHotel.id}/rooms/${roomId}`, { method: 'DELETE' });
      if (response.ok) {
        showNotification('Room deleted successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        showNotification('Failed to delete room', 'error');
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
      const response = await fetch('/api/reservations', {
        headers: { 'X-User-Id': userId }
      });
      const data = await response.json();
      if (Array.isArray(data)) {
        setReservations(data);
        showNotification(`Found ${data.length} reservations`, 'info');
      } else {
        console.error('Expected reservations array but got:', data);
        showNotification('Failed to load reservations', 'error');
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
      const response = await fetch('/api/reservations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newReservation),
      });
      if (response.ok) {
        setNewReservation({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '' });
        showNotification('Reservation created successfully!', 'success');
      } else {
        showNotification('Failed to create reservation', 'error');
      }
    } catch (error) {
      console.error('Failed to create reservation:', error);
      showNotification('Error creating reservation', 'error');
    }
  };

  const handleCancelReservation = async (id: string) => {
    try {
      const response = await fetch(`/api/reservations/${id}`, { method: 'DELETE' });
      if (response.ok) {
        showNotification('Reservation cancelled successfully!', 'success');
        if (userId) {
          // Refresh if user ID is entered
          const refreshResponse = await fetch('/api/reservations', { headers: { 'X-User-Id': userId } });
          const data = await refreshResponse.json();
          if (Array.isArray(data)) {
            setReservations(data);
          } else {
            setReservations([]);
          }
        }
      } else {
        showNotification('Failed to cancel reservation', 'error');
      }
    } catch (error) {
      console.error('Failed to cancel reservation:', error);
      showNotification('Error cancelling reservation', 'error');
    }
  };

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif', maxWidth: '1200px', margin: '0 auto' }}>
      <h1>Hotel Management System - API Testing Tool</h1>

      {/* Notifications */}
      <div style={{
        position: 'fixed',
        top: '1rem',
        right: '1rem',
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
        zIndex: 1000
      }}>
        {notifications.map(n => (
          <div key={n.id} style={{
            padding: '1rem',
            borderRadius: '4px',
            color: 'white',
            backgroundColor: n.type === 'success' ? '#4caf50' : n.type === 'error' ? '#f44336' : '#2196f3',
            boxShadow: '0 2px 5px rgba(0,0,0,0.2)',
            minWidth: '250px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <span>{n.message}</span>
            <button onClick={() => setNotifications(prev => prev.filter(notif => notif.id !== n.id))} style={{
              background: 'none',
              border: 'none',
              color: 'white',
              cursor: 'pointer',
              fontWeight: 'bold',
              fontSize: '1.2rem',
              marginLeft: '1rem'
            }}>×</button>
          </div>
        ))}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
        
        {/* HOTEL MANAGEMENT */}
        <section style={{ border: '1px solid #ddd', padding: '1rem', borderRadius: '8px' }}>
          <h2>Hotels</h2>
          
          <div style={{ marginBottom: '1.5rem' }}>
            <h3>Search Hotels</h3>
            <form onSubmit={handleSearchHotels} style={{ display: 'flex', gap: '0.5rem' }}>
              <input 
                type="text" 
                placeholder="City" 
                value={searchCity} 
                onChange={(e) => setSearchCity(e.target.value)} 
                style={{ flex: 1, padding: '0.5rem' }} 
              />
              <button type="submit">Search</button>
              <button type="button" onClick={() => { setSearchCity(''); fetchHotels(); }}>Clear</button>
            </form>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <h3>{editingHotel ? 'Edit Hotel' : 'Add New Hotel'}</h3>
            <form onSubmit={editingHotel ? handleUpdateHotel : handleAddHotel} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <input 
                type="text" 
                placeholder="Name" 
                value={editingHotel ? editingHotel.name : newHotel.name} 
                onChange={(e) => editingHotel ? setEditingHotel({...editingHotel, name: e.target.value}) : setNewHotel({...newHotel, name: e.target.value})} 
                required 
                style={{ padding: '0.5rem' }} 
              />
              <input 
                type="text" 
                placeholder="City" 
                value={editingHotel ? editingHotel.city : newHotel.city} 
                onChange={(e) => editingHotel ? setEditingHotel({...editingHotel, city: e.target.value}) : setNewHotel({...newHotel, city: e.target.value})} 
                required 
                style={{ padding: '0.5rem' }} 
              />
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button type="submit" style={{ flex: 1 }}>{editingHotel ? 'Update Hotel' : 'Add Hotel'}</button>
                {editingHotel && <button type="button" onClick={() => setEditingHotel(null)}>Cancel</button>}
              </div>
            </form>
          </div>

          <div>
            <h3>Hotels List</h3>
            {loadingHotels ? <p>Loading...</p> : (
              <ul style={{ listStyle: 'none', padding: 0 }}>
                {Array.isArray(hotels) && hotels.map(hotel => (
                  <li key={hotel.id} style={{ 
                    padding: '0.8rem', borderBottom: '1px solid #eee', 
                    backgroundColor: selectedHotel?.id === hotel.id ? '#f0f7ff' : 'transparent',
                    display: 'flex', justifyContent: 'space-between', alignItems: 'center'
                  }}>
                    <div onClick={() => setSelectedHotel(hotel)} style={{ cursor: 'pointer', flex: 1 }}>
                      <strong>{hotel.name}</strong> - {hotel.city} <br/>
                      <small style={{ color: '#666' }}>ID: {hotel.id}</small>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button onClick={() => setEditingHotel(hotel)}>Edit</button>
                      <button onClick={() => handleDeleteHotel(hotel.id)} style={{ color: 'red' }}>Del</button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </section>

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
                  <input 
                    type="text" 
                    placeholder="Type ID" 
                    value={editingRoom ? editingRoom.typeId : newRoom.typeId} 
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, typeId: e.target.value}) : setNewRoom({...newRoom, typeId: e.target.value})} 
                    required 
                    style={{ padding: '0.5rem' }} 
                  />
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