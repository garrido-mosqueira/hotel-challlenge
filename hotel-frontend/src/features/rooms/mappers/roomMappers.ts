import { NewRoomInput, Room } from '@/entities';

type RoomApiShape = Omit<Room, 'available'> & {
  isAvailable?: boolean;
  available?: boolean;
};

type RoomWriteShape = Omit<Room, 'available'> & {
  isAvailable: boolean;
};

type NewRoomWriteShape = NewRoomInput & {
  isAvailable: boolean;
};

export function toRoomModel(input: RoomApiShape): Room {
  return {
    ...input,
    available: input.isAvailable ?? input.available ?? false,
  };
}

export function toRoomModels(input: RoomApiShape[]): Room[] {
  return input.map(toRoomModel);
}

export function toRoomWritePayload(input: Room): RoomWriteShape {
  return {
    ...input,
    isAvailable: input.available,
  };
}

export function toNewRoomWritePayload(input: NewRoomInput): NewRoomWriteShape {
  return {
    ...input,
    isAvailable: input.available,
  };
}

