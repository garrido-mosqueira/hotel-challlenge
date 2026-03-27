# Hotel Management Challenge

A comprehensive hotel management system built with a modern tech stack, featuring a **Hexagonal Architecture (Ports and Adapters)** for the backend and a React-based frontend.

## 🏗 Project Structure

The project follows a clean architecture approach, separating concerns into distinct modules:

```
hotel-challlenge/
├── hotel-domain        # Core business logic and domain entities — framework-free
├── hotel-application   # Use cases and application services
├── hotel-persistence   # Data storage adapters (PostgreSQL, Redis, RabbitMQ)
├── hotel-api           # RESTful API layer (Spring Boot entrypoint)
└── hotel-frontend      # Web interface (Next.js + TypeScript)
```

## 🛠 Technologies Used

### Backend
- **Java 21**
- **Spring Boot 3.2.0**
- **PostgreSQL 15** — Primary database
- **Redis 7** — Caching
- **RabbitMQ 3** — Async payment processing
- **Flyway** — Database migrations
- **Lombok** — Boilerplate reduction
- **MapStruct** — Object mapping
- **JUnit 5 & Testcontainers** — Integration testing

### Frontend
- **Next.js**
- **React**
- **TypeScript**

### Infrastructure
- **Docker & Docker Compose**

---

## 🚀 Getting Started

### Prerequisites
- Docker and Docker Compose
- JDK 21 (for manual builds)
- Maven (for manual builds)
- Node.js (for frontend development)

---

### Option A — Docker Compose (recommended)

Starts PostgreSQL, Redis, RabbitMQ and the Spring Boot API all together:

```bash
docker-compose up --build
```

Services started:
| Service    | Port   |
|------------|--------|
| PostgreSQL | `5432` |
| Redis      | `6379` |
| RabbitMQ   | `5672` (AMQP) / `15672` (management UI) |
| API        | `8080` |

---

### Option B — Manual Backend Setup

1. Start infrastructure:
   ```bash
   docker-compose up -d db redis rabbitmq
   ```

2. Build all modules from the root directory:
   ```bash
   ./mvnw clean install -DskipTests
   ```

3. Run the API from the root directory:
   ```bash
   ./mvnw spring-boot:run
   ```

4. (Alternative) Run the packaged JAR:
   ```bash
   java -jar hotel-api/target/hotel-api-1.1.0.jar
   ```

> **Note:** Ensure PostgreSQL, Redis and RabbitMQ are running before starting the API. Connection defaults are in `hotel-api/src/main/resources/application.yml`.

---

### Frontend Setup

1. Navigate to `hotel-frontend`:
   ```bash
   cd hotel-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the development server:
   ```bash
   npm run dev
   ```

Frontend available at `http://localhost:3000`. Requests to `/api/*` are proxied to the backend at `http://localhost:8080`.

---

## 📡 API Endpoints

### Hotels

| Method   | Endpoint                        | Description              |
|----------|---------------------------------|--------------------------|
| `GET`    | `/api/hotels`                   | List all hotels          |
| `GET`    | `/api/hotels/{id}`              | Get hotel by ID          |
| `POST`   | `/api/hotels`                   | Create a hotel           |
| `PUT`    | `/api/hotels/{id}`              | Update a hotel           |
| `DELETE` | `/api/hotels/{id}`              | Delete a hotel           |
| `GET`    | `/api/hotels/search?city={city}`| Search hotels by city    |

### Rooms

| Method   | Endpoint                                  | Description              |
|----------|-------------------------------------------|--------------------------|
| `GET`    | `/api/hotels/{hotelId}/rooms`             | List rooms for a hotel   |
| `GET`    | `/api/hotels/{hotelId}/rooms/{roomId}`    | Get room by ID           |
| `POST`   | `/api/hotels/{hotelId}/rooms`             | Add a room to a hotel    |
| `PUT`    | `/api/hotels/{hotelId}/rooms/{roomId}`    | Update a room            |
| `DELETE` | `/api/hotels/{hotelId}/rooms/{roomId}`    | Delete a room            |

### Reservations

| Method   | Endpoint                     | Description                                      |
|----------|------------------------------|--------------------------------------------------|
| `GET`    | `/api/reservations`          | List reservations (requires `X-User-Id` header)  |
| `GET`    | `/api/reservations/{id}`     | Get reservation by ID                            |
| `POST`   | `/api/reservations`          | Create a reservation                             |
| `DELETE` | `/api/reservations/{id}`     | Cancel a reservation                             |

---

## 🔄 Reservation Status Flow

```
PENDING ──► CONFIRMED ──► REFUNDED
   │
   └──► CANCELLED
```

| Transition             | Trigger                                       |
|------------------------|-----------------------------------------------|
| `PENDING → CONFIRMED`  | Async payment processing completes            |
| `CONFIRMED → REFUNDED` | Cancel called on a confirmed reservation      |
| `PENDING → CANCELLED`  | Cancel called on a pending reservation        |

> Cancelling an already `REFUNDED` reservation is idempotent (stays `REFUNDED`).  
> Cancelling an already `CANCELLED` reservation throws an error.

---

## 🧪 Testing

Run all backend tests:
```bash
./mvnw test
```

Run a specific module's tests:
```bash
./mvnw -pl hotel-application test
./mvnw -pl hotel-api -am test
```

Integration tests use **Testcontainers** to spin up real PostgreSQL and Redis instances, ensuring high fidelity with the production environment.

---

## 📄 Postman Collection

A Postman collection is available in the root directory:

```
hotel-api.postman_collection.json
```
