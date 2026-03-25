# Hotel Management Challenge

A comprehensive hotel management system built with a modern tech stack, featuring a Hexagonal Architecture (Ports and Adapters) for the backend and a React-based frontend.

## 🏗 Project Structure

The project follows a clean architecture approach, separating concerns into distinct modules:

- **hotel-domain**: Contains the core business logic and domain entities. It is independent of any frameworks.
- **hotel-application**: Implements the use cases and coordinates the domain logic.
- **hotel-persistence**: Handles data storage using PostgreSQL and Redis.
- **hotel-api**: Provides the RESTful API endpoints using Spring Boot.
- **hotel-frontend**: A modern web interface built with Next.js.

## 🛠 Technologies Used

### Backend
- **Java 21**
- **Spring Boot 3.2.0**
- **PostgreSQL** (Primary database)
- **Redis** (Caching and simulation)
- **Lombok**
- **MapStruct** (Object mapping)
- **JUnit 5 & Testcontainers** (Testing)

### Frontend
- **Next.js**
- **React**
- **TypeScript**

### Infrastructure
- **Docker & Docker Compose**

## 🚀 Getting Started

### Prerequisites
- Docker and Docker Compose
- JDK 21 (for manual builds)
- Maven (for manual builds)
- Node.js (for frontend development)

### Running with Docker Compose

The easiest way to get the entire system up and running is using Docker Compose:

```bash
docker-compose up --build
```

This will start:
- PostgreSQL on port `5432`
- Redis on port `6379`
- Spring Boot API on port `8080`

### Manual Backend Setup

1. Navigate to the root directory.
2. Build the project using Maven:
   ```bash
   ./mvnw clean install
   ```
3. Run the API module:
   ```bash
   java -jar hotel-api/target/hotel-java-1.1.0.jar
   ```
   *Note: Ensure PostgreSQL and Redis are running and correctly configured in `application.properties`.*

### Frontend Setup

1. Navigate to the `hotel-frontend` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
   The frontend will be available at `http://localhost:3000`.

## 📡 API Endpoints

### Hotels
- `GET /api/hotels` - List all hotels
- `GET /api/hotels/{id}` - Get hotel details
- `POST /api/hotels` - Add a new hotel
- `PUT /api/hotels/{id}` - Update a hotel
- `DELETE /api/hotels/{id}` - Delete a hotel
- `GET /api/hotels/search?city={city}` - Search hotels by city

### Rooms
- `GET /api/hotels/{hotelId}/rooms` - List rooms for a hotel
- `POST /api/hotels/{hotelId}/rooms` - Add a room to a hotel
- `DELETE /api/hotels/{hotelId}/rooms/{roomId}` - Remove a room

### Reservations
- `GET /api/reservations` - List reservations (requires `X-User-Id` header)
- `POST /api/reservations` - Create a new reservation
- `DELETE /api/reservations/{id}` - Cancel a reservation

## 🧪 Testing

The project includes unit, integration, and architecture tests.

To run backend tests:
```bash
./mvnw test
```

Integration tests use **Testcontainers** to spin up real PostgreSQL and Redis instances, ensuring high fidelity with the production environment.

## 📄 Postman Collection

A Postman collection is provided in the root directory (`hotel-api.postman_collection.json`) to help you test the API endpoints easily.
