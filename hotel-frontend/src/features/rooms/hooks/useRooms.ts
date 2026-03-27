import { FormEvent, useEffect, useState } from 'react';
import { Hotel, NewRoomInput, NotificationType, Room } from '@/entities';
import { parseErrorMessage } from '@/shared/services/errorParser';
import { roomsService } from '@/features/rooms/services/roomsService';
import { toNewRoomWritePayload, toRoomModels, toRoomWritePayload } from '@/features/rooms/mappers/roomMappers';

type UseRoomsOptions = {
  selectedHotel: Hotel | null;
  notify: (message: string, type?: NotificationType) => void;
};

const EMPTY_NEW_ROOM: NewRoomInput = { number: '', typeId: 'SINGLE', floor: 0, name: '', available: true };

export function useRooms({ selectedHotel, notify }: UseRoomsOptions) {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [newRoom, setNewRoom] = useState<NewRoomInput>(EMPTY_NEW_ROOM);
  const [loadingRooms, setLoadingRooms] = useState(false);
  const [editingRoom, setEditingRoom] = useState<Room | null>(null);

  const handleError = async (response: Response, defaultMessage: string) => {
    const message = await parseErrorMessage(response, defaultMessage);
    notify(message, 'error');
  };

  const fetchRooms = async (hotelId: string) => {
    setLoadingRooms(true);
    try {
      const response = await roomsService.listByHotel(hotelId);
      if (response.ok) {
        const data = await response.json();
        setRooms(Array.isArray(data) ? toRoomModels(data) : []);
      } else {
        await handleError(response, 'Failed to load rooms');
        setRooms([]);
      }
    } catch (error) {
      console.error('Failed to fetch rooms:', error);
      notify('Failed to fetch rooms', 'error');
      setRooms([]);
    } finally {
      setLoadingRooms(false);
    }
  };

  useEffect(() => {
    if (selectedHotel) {
      fetchRooms(selectedHotel.id);
      return;
    }

    setRooms([]);
    setEditingRoom(null);
  }, [selectedHotel]);

  const handleAddRoom = async (e: FormEvent) => {
    e.preventDefault();
    if (!selectedHotel) return;

    try {
      const response = await roomsService.create(selectedHotel.id, toNewRoomWritePayload(newRoom));
      if (response.ok) {
        setNewRoom(EMPTY_NEW_ROOM);
        notify('Room added successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        await handleError(response, 'Failed to add room');
      }
    } catch (error) {
      console.error('Failed to add room:', error);
      notify('Error adding room', 'error');
    }
  };

  const handleUpdateRoom = async (e: FormEvent) => {
    e.preventDefault();
    if (!editingRoom || !selectedHotel) return;

    try {
      const response = await roomsService.update(selectedHotel.id, editingRoom.id, toRoomWritePayload(editingRoom));
      if (response.ok) {
        setEditingRoom(null);
        notify('Room updated successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        await handleError(response, 'Failed to update room');
      }
    } catch (error) {
      console.error('Failed to update room:', error);
      notify('Error updating room', 'error');
    }
  };

  const handleDeleteRoom = async (roomId: string) => {
    if (!selectedHotel) return;

    try {
      const response = await roomsService.remove(selectedHotel.id, roomId);
      if (response.ok) {
        notify('Room deleted successfully!', 'success');
        fetchRooms(selectedHotel.id);
      } else {
        await handleError(response, 'Failed to delete room');
      }
    } catch (error) {
      console.error('Failed to delete room:', error);
      notify('Error deleting room', 'error');
    }
  };

  return {
    rooms,
    newRoom,
    setNewRoom,
    loadingRooms,
    editingRoom,
    setEditingRoom,
    handleAddRoom,
    handleUpdateRoom,
    handleDeleteRoom,
  };
}

