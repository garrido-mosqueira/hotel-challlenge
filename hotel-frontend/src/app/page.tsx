'use client';

import { useEffect, useState } from 'react';

type Hotel = {
  id: string;
  name: string;
  city: string;
};

export default function Home() {
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [newHotel, setNewHotel] = useState({ name: '', city: '' });
  const [loading, setLoading] = useState(true);

  const fetchHotels = async () => {
    try {
      const response = await fetch('/api/hotels');
      const data = await response.json();
      setHotels(data);
    } catch (error) {
      console.error('Failed to fetch hotels:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHotels();
  }, []);

  const handleAddHotel = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/hotels', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
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

  const handleDelete = async (id: string) => {
    try {
      const response = await fetch(`/api/hotels/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        fetchHotels();
      }
    } catch (error) {
      console.error('Failed to delete hotel:', error);
    }
  };

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Hotel Management API Testing</h1>
      
      <div style={{ marginBottom: '2rem' }}>
        <h2>Add New Hotel</h2>
        <form onSubmit={handleAddHotel} style={{ display: 'flex', gap: '1rem' }}>
          <input
            type="text"
            placeholder="Hotel Name"
            value={newHotel.name}
            onChange={(e) => setNewHotel({ ...newHotel, name: e.target.value })}
            required
            style={{ padding: '0.5rem' }}
          />
          <input
            type="text"
            placeholder="City"
            value={newHotel.city}
            onChange={(e) => setNewHotel({ ...newHotel, city: e.target.value })}
            required
            style={{ padding: '0.5rem' }}
          />
          <button type="submit" style={{ padding: '0.5rem 1rem' }}>Add Hotel</button>
        </form>
      </div>

      <div>
        <h2>Hotels List</h2>
        {loading ? (
          <p>Loading...</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {hotels.map((hotel) => (
              <li 
                key={hotel.id} 
                style={{ 
                  border: '1px solid #ccc', 
                  margin: '0.5rem 0', 
                  padding: '1rem',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}
              >
                <div>
                  <strong>{hotel.name}</strong> - {hotel.city}
                </div>
                <button 
                  onClick={() => handleDelete(hotel.id)}
                  style={{ backgroundColor: 'red', color: 'white', border: 'none', padding: '0.5rem', cursor: 'pointer' }}
                >
                  Delete
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}