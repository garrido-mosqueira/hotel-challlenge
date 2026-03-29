# Hotel Challenge API - Recreate Guide

Use this file as the minimum blueprint to recreate the backend API.

## 1) Stack
- Java 21
- Spring Boot 3.2.0
- Maven (multi-module project)
- PostgreSQL 15, Redis 7, RabbitMQ 3

## 2) Modules (Hexagonal)
- `hotel-domain`: entities, business rules, reservation state transitions
- `hotel-application`: use cases and orchestration services
- `hotel-persistence`: adapters for DB/cache/messaging
- `hotel-api`: REST controllers + Spring Boot entrypoint

## 3) Infra and Ports
- API: `8080`
- PostgreSQL: `5432`
- Redis: `6379`
- RabbitMQ: `5672` (`15672` management UI)

Start infra only:
```bash
docker-compose up -d db redis rabbitmq
```

## 4) Build, Run, Test
From repo root:
```bash
mvn clean install
mvn -pl hotel-api spring-boot:run
mvn test
```

Or run full stack with Docker:
```bash
docker-compose up --build
```

## 5) API Surface to Recreate
- Hotels: CRUD + city search
  - `/api/hotels`
  - `/api/hotels/{id}`
  - `/api/hotels/search?city={city}`
- Rooms (nested under hotel)
  - `/api/hotels/{hotelId}/rooms`
  - `/api/hotels/{hotelId}/rooms/{roomId}`
- Reservations
  - `/api/reservations` (list requires `X-User-Id`)
  - `/api/reservations/{id}`

## 6) Reservation Rules (must match)
- Flow: `PENDING -> CONFIRMED -> REFUNDED`
- `PENDING -> CANCELLED` on cancel
- Cancelling `REFUNDED` is idempotent
- Cancelling `CANCELLED` returns error

## 7) Rebuild Checklist
- Create the 4 Maven modules above
- Keep domain logic framework-free in `hotel-domain`
- Implement ports in domain/application, adapters in persistence/api
- Wire PostgreSQL + Redis + RabbitMQ in Spring config
- Expose controllers for hotels, rooms, reservations
- Add tests (unit + integration with Testcontainers)
