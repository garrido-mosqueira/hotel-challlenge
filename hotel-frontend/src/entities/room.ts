export type Room = {
  id: string;
  hotelId: string;
  number: string;
  typeId: string;
  floor: number;
  name: string;
  available: boolean;
};

export type NewRoomInput = {
  number: string;
  typeId: string;
  floor: number;
  name: string;
  available: boolean;
};

