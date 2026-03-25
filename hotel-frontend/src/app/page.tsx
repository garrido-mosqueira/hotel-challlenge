'use client';

import { useEffect, useState } from 'react';

type Hotel = {
  hotelId: string;
  name: string;
  city: string;
};

type Room = {
  id: string;
  hotelId: string;
  roomNumber: string;
  type: string;
  price: number;
  available: boolean;
};

type Reservation = {
  id: string;
  guestId: string;
  roomId: string;
  checkInDate: string;
  checkOutDate: string;
  status: string;
};

export default function Home() {
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [newHotel, setNewHotel] = useState({ name: '', city: '' });
  const [searchCity, setSearchCity] = useState('');
  const [loadingHotels, setLoadingHotels] = useState(true);

  const [selectedHotel, setSelectedHotel] = useState<Hotel | null>(null);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [newRoom, setNewRoom] = useState({ roomNumber: '', type: '', price: 0, available: true });
  const [loadingRooms, setLoadingRooms] = useState(false);

  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [userId, setUserId] = useState('');
  const [newReservation, setNewReservation] = useState({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '' });
  const [loadingReservations, setLoadingReservations] = useState(false);

  const [editingHotel, setEditingHotel] = useState<Hotel | null>(null);
  const [editingRoom, setEditingRoom] = useState<Room | null>(null);

  useEffect(() => {
    fetchHotels();
  }, []);

  useEffect(() => {
    if (selectedHotel) {
      fetchRooms(selectedHotel.hotelId);
    } else {
      setRooms([]);
    }
  }, [selectedHotel]);

  // Hotel APIs
  const fetchHotels = async () => {
    setLoadingHotels(true);
    try {
      const response = await fetch('/api/hotels');
      const data = await response.json();
      setHotels(data);
    } catch (error) {
      console.error('Failed to fetch hotels:', error);
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
      const response = await fetch(`/api/hotels/search?city=${searchCity}`);
      const data = await response.json();
      setHotels(data);
    } catch (error) {
      console.error('Failed to search hotels:', error);
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
        fetchHotels();
      }
    } catch (error) {
      console.error('Failed to add hotel:', error);
    }
  };

  const handleUpdateHotel = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingHotel) return;
    try {
      const response = await fetch(`/api/hotels/${editingHotel.hotelId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(editingHotel),
      });
      if (response.ok) {
        setEditingHotel(null);
        fetchHotels();
      }
    } catch (error) {
      console.error('Failed to update hotel:', error);
    }
  };

  const handleDeleteHotel = async (hotelId: string) => {
    try {
      const response = await fetch(`/api/hotels/${hotelId}`, { method: 'DELETE' });
      if (response.ok) {
        if (selectedHotel?.hotelId === hotelId) setSelectedHotel(null);
        fetchHotels();
      }
    } catch (error) {
      console.error('Failed to delete hotel:', error);
    }
  };

  // Room APIs
  const fetchRooms = async (hotelId: string) => {
    setLoadingRooms(true);
    try {
      const response = await fetch(`/api/hotels/${hotelId}/rooms`);
      const data = await response.json();
      setRooms(data);
    } catch (error) {
      console.error('Failed to fetch rooms:', error);
    } finally {
      setLoadingRooms(false);
    }
  };

  const handleAddRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedHotel) return;
    try {
      const response = await fetch(`/api/hotels/${selectedHotel.hotelId}/rooms`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newRoom),
      });
      if (response.ok) {
        setNewRoom({ roomNumber: '', type: '', price: 0, available: true });
        fetchRooms(selectedHotel.hotelId);
      }
    } catch (error) {
      console.error('Failed to add room:', error);
    }
  };

  const handleUpdateRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingRoom || !selectedHotel) return;
    try {
      const response = await fetch(`/api/hotels/${selectedHotel.hotelId}/rooms/${editingRoom.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(editingRoom),
      });
      if (response.ok) {
        setEditingRoom(null);
        fetchRooms(selectedHotel.hotelId);
      }
    } catch (error) {
      console.error('Failed to update room:', error);
    }
  };

  const handleDeleteRoom = async (roomId: string) => {
    if (!selectedHotel) return;
    try {
      const response = await fetch(`/api/hotels/${selectedHotel.hotelId}/rooms/${roomId}`, { method: 'DELETE' });
      if (response.ok) {
        fetchRooms(selectedHotel.hotelId);
      }
    } catch (error) {
      console.error('Failed to delete room:', error);
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
      setReservations(data);
    } catch (error) {
      console.error('Failed to fetch reservations:', error);
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
        alert('Reservation created successfully!');
      }
    } catch (error) {
      console.error('Failed to create reservation:', error);
    }
  };

  const handleCancelReservation = async (id: string) => {
    try {
      const response = await fetch(`/api/reservations/${id}`, { method: 'DELETE' });
      if (response.ok) {
        if (userId) {
          // Refresh if user ID is entered
          const refreshResponse = await fetch('/api/reservations', { headers: { 'X-User-Id': userId } });
          setReservations(await refreshResponse.json());
        }
      }
    } catch (error) {
      console.error('Failed to cancel reservation:', error);
    }
  };

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif', maxWidth: '1200px', margin: '0 auto' }}>
      <h1>Hotel Management System - API Testing Tool</h1>

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
                {hotels.map(hotel => (
                  <li key={hotel.hotelId} style={{ 
                    padding: '0.8rem', borderBottom: '1px solid #eee', 
                    backgroundColor: selectedHotel?.hotelId === hotel.hotelId ? '#f0f7ff' : 'transparent',
                    display: 'flex', justifyContent: 'space-between', alignItems: 'center'
                  }}>
                    <div onClick={() => setSelectedHotel(hotel)} style={{ cursor: 'pointer', flex: 1 }}>
                      <strong>{hotel.name}</strong> - {hotel.city} <br/>
                      <small style={{ color: '#666' }}>ID: {hotel.hotelId}</small>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button onClick={() => setEditingHotel(hotel)}>Edit</button>
                      <button onClick={() => handleDeleteHotel(hotel.hotelId)} style={{ color: 'red' }}>Del</button>
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
                    value={editingRoom ? editingRoom.roomNumber : newRoom.roomNumber} 
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, roomNumber: e.target.value}) : setNewRoom({...newRoom, roomNumber: e.target.value})} 
                    required 
                    style={{ padding: '0.5rem' }} 
                  />
                  <input 
                    type="text" 
                    placeholder="Type (e.g. SINGLE, DOUBLE)" 
                    value={editingRoom ? editingRoom.type : newRoom.type} 
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, type: e.target.value}) : setNewRoom({...newRoom, type: e.target.value})} 
                    required 
                    style={{ padding: '0.5rem' }} 
                  />
                  <input 
                    type="number" 
                    placeholder="Price" 
                    value={editingRoom ? editingRoom.price : newRoom.price} 
                    onChange={(e) => editingRoom ? setEditingRoom({...editingRoom, price: Number(e.target.value)}) : setNewRoom({...newRoom, price: Number(e.target.value)})} 
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
                    {rooms.map(room => (
                      <li key={room.id} style={{ padding: '0.8rem', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                          <strong>Room {room.roomNumber}</strong> ({room.type}) - ${room.price} <br/>
                          <small style={{ color: room.available ? 'green' : 'red' }}>{room.available ? 'Available' : 'Booked'}</small> <br/>
                          <small style={{ color: '#666' }}>ID: {room.id}</small>
                        </div>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button onClick={() => {
                            setNewReservation({ ...newReservation, roomId: room.id });
                            window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
                          }}>Book</button>
                          <button onClick={() => setEditingRoom(room)}>Edit</button>
                          <button onClick={() => handleDeleteRoom(room.id)} style={{ color: 'red' }}>Del</button>
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
                  {reservations.map(res => (
                    <li key={res.id} style={{ padding: '0.8rem', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div>
                        <strong>{res.id}</strong> ({res.status}) <br/>
                        <small>{res.checkInDate} to {res.checkOutDate}</small> <br/>
                        <small style={{ color: '#666' }}>Room: {res.roomId}</small>
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