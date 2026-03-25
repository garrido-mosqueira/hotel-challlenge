-- Create the hotels table
CREATE TABLE hotels (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    city VARCHAR(255)
);

-- Create the rooms table
CREATE TABLE rooms (
    id VARCHAR(255) PRIMARY KEY,
    number VARCHAR(255),
    type_id VARCHAR(255),
    floor INT,
    name VARCHAR(255),
    is_available BOOLEAN,
    hotel_id VARCHAR(255),
    CONSTRAINT fk_room_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE
);

-- Create the room_type_inventory table
CREATE TABLE room_type_inventory (
    id VARCHAR(255) PRIMARY KEY,
    date DATE,
    room_type_id VARCHAR(255),
    total_inventory INT,
    total_reserved INT,
    hotel_id VARCHAR(255),
    CONSTRAINT fk_inventory_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE
);

-- Create the reservations table
CREATE TABLE reservations (
    id VARCHAR(255) PRIMARY KEY,
    guest_id VARCHAR(255),
    room_id VARCHAR(255),
    room_name VARCHAR(255),
    check_in_date DATE,
    check_out_date DATE,
    status VARCHAR(255)
);
