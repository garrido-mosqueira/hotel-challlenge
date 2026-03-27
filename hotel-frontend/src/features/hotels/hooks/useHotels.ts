import { FormEvent, useEffect, useState } from 'react';
import { Hotel, NewHotelInput, NotificationType } from '@/entities';
import { parseErrorMessage } from '@/shared/services/errorParser';
import { hotelsService } from '@/features/hotels/services/hotelsService';

type UseHotelsOptions = {
  notify: (message: string, type?: NotificationType) => void;
};

export function useHotels({ notify }: UseHotelsOptions) {
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [newHotel, setNewHotel] = useState<NewHotelInput>({ name: '', city: '' });
  const [searchCity, setSearchCity] = useState('');
  const [loadingHotels, setLoadingHotels] = useState(true);
  const [selectedHotel, setSelectedHotel] = useState<Hotel | null>(null);
  const [editingHotel, setEditingHotel] = useState<Hotel | null>(null);

  const handleError = async (response: Response, defaultMessage: string) => {
    const message = await parseErrorMessage(response, defaultMessage);
    notify(message, 'error');
  };

  const fetchHotels = async () => {
    setLoadingHotels(true);
    try {
      const response = await hotelsService.list();
      if (response.ok) {
        const data = await response.json();
        setHotels(Array.isArray(data) ? data : []);
      } else {
        await handleError(response, 'Failed to load hotels');
        setHotels([]);
      }
    } catch (error) {
      console.error('Failed to fetch hotels:', error);
      notify('Failed to fetch hotels', 'error');
      setHotels([]);
    } finally {
      setLoadingHotels(false);
    }
  };

  useEffect(() => {
    fetchHotels();
  }, []);

  const handleSearchHotels = async (e: FormEvent) => {
    e.preventDefault();
    if (!searchCity) {
      fetchHotels();
      return;
    }

    setLoadingHotels(true);
    try {
      const response = await hotelsService.searchByCity(searchCity);
      if (response.ok) {
        const data = await response.json();
        setHotels(Array.isArray(data) ? data : []);
        if (Array.isArray(data)) notify(`Found ${data.length} hotels`, 'info');
      } else {
        await handleError(response, 'Search failed');
        setHotels([]);
      }
    } catch (error) {
      console.error('Failed to search hotels:', error);
      notify('Failed to search hotels', 'error');
      setHotels([]);
    } finally {
      setLoadingHotels(false);
    }
  };

  const clearSearch = () => {
    setSearchCity('');
    fetchHotels();
  };

  const handleAddHotel = async (e: FormEvent) => {
    e.preventDefault();
    try {
      const response = await hotelsService.create(newHotel);
      if (response.ok) {
        setNewHotel({ name: '', city: '' });
        notify('Hotel added successfully!', 'success');
        fetchHotels();
      } else {
        await handleError(response, 'Failed to add hotel');
      }
    } catch (error) {
      console.error('Failed to add hotel:', error);
      notify('Error adding hotel', 'error');
    }
  };

  const handleUpdateHotel = async (e: FormEvent) => {
    e.preventDefault();
    if (!editingHotel) return;

    try {
      const response = await hotelsService.update(editingHotel);
      if (response.ok) {
        setEditingHotel(null);
        notify('Hotel updated successfully!', 'success');
        fetchHotels();
      } else {
        await handleError(response, 'Failed to update hotel');
      }
    } catch (error) {
      console.error('Failed to update hotel:', error);
      notify('Error updating hotel', 'error');
    }
  };

  const handleDeleteHotel = async (hotelId: string) => {
    try {
      const response = await hotelsService.remove(hotelId);
      if (response.ok) {
        if (selectedHotel?.id === hotelId) setSelectedHotel(null);
        notify('Hotel deleted successfully!', 'success');
        fetchHotels();
      } else {
        await handleError(response, 'Failed to delete hotel');
      }
    } catch (error) {
      console.error('Failed to delete hotel:', error);
      notify('Error deleting hotel', 'error');
    }
  };

  return {
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
  };
}

