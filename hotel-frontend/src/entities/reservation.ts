export type Reservation = {
  id: string;
  guestId: string;
  roomId: string;
  roomName: string;
  checkInDate: string;
  checkOutDate: string;
  status: string;
};

export type NewReservationInput = {
  guestId: string;
  roomId: string;
  checkInDate: string;
  checkOutDate: string;
};

