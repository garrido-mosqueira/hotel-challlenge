-- Fix for: column "id" cannot be cast automatically to type uuid
CREATE TABLE IF NOT EXISTS reservations (
    id varchar(255) PRIMARY KEY,
    guestId varchar(255),
    roomId varchar(255),
    checkInDate date,
    checkOutDate date,
    status varchar(255)
);
^^
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'reservations' 
        AND column_name = 'id' 
        AND data_type = 'character varying'
    ) THEN
        ALTER TABLE reservations ALTER COLUMN id SET DATA TYPE uuid USING id::uuid;
    END IF;
END $$;
^^
