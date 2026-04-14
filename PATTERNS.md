# 🏗️ Design Patterns and Architectural Overview

This document provides an academic perspective on the design patterns and architectural decisions implemented in the Hotel Challenge project.

## 1. ⬡ Hexagonal Architecture (Ports and Adapters)

### Intent
To create loosely coupled application components that can be easily connected to their software environment by means of ports and adapters. This makes the core logic independent of external details like databases, UI, or message brokers.

### Implementation
The project is structured into several modules that enforce this separation:
- **`hotel-domain`**: The core "Inside". It contains the business logic, domain models ([`Hotel`](hotel-domain/src/main/java/com/fran/hotel/domain/model/Hotel.java), [`Reservation`](hotel-domain/src/main/java/com/fran/hotel/domain/model/Reservation.java)), and **Ports** (interfaces like [`HotelPersistencePort`](hotel-domain/src/main/java/com/fran/hotel/domain/port/HotelPersistencePort.java), [`ReservationUseCase`](hotel-domain/src/main/java/com/fran/hotel/domain/port/ReservationUseCase.java)).
- **`hotel-application`**: Orchestrates the flow of data to and from the domain. It implements the "Inbound" ports ([`HotelUseCase`](hotel-domain/src/main/java/com/fran/hotel/domain/port/HotelUseCase.java)) via services.
- **`hotel-persistence` / `hotel-api`**: The "Outside" or **Adapters**. 
    - `hotel-persistence` implements the "Outbound" ports using Spring Data JPA.
    - `hotel-api` adapts HTTP requests to call the application services.

### Academic Perspective
This architecture adheres to the **Dependency Inversion Principle (DIP)**. By depending on abstractions (Ports) rather than concretions (Adapters), the system becomes highly testable and flexible. One could swap the PostgreSQL database for a NoSQL one by simply adding a new adapter, without touching the business logic.

### 📚 Resources
- [Alistair Cockburn - Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Netflix Tech Blog - Ready for Changes with Hexagonal Architecture](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967732)

---

## 2. 🧩 Strategy Pattern (Execution Models)

### Intent
To define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

### Implementation: Virtual vs. Platform Threads
The project leverages Java 21 features to manage concurrency. While often configured at the JVM or Spring Boot level, the choice between **Platform Threads** and **Virtual Threads (Project Loom)** represents a Concurrency Strategy:

- **Platform Strategy**: Mapping application threads 1:1 to OS threads. Suitable for CPU-bound tasks but expensive in terms of memory and context switching for high-concurrency I/O.
- **Virtual Strategy**: Using lightweight virtual threads that are scheduled by the JVM onto a small set of platform threads. This is the "Strategic" choice for high-throughput I/O-bound applications like a hotel reservation system.

In this project, the [`TaskScheduler`](hotel-api/src/main/java/com/fran/hotel/api/configuration/RabbitMQConfiguration.java) and [`RabbitMQPaymentConsumer`](hotel-persistence/src/main/java/com/fran/hotel/persistence/adapter/RabbitMQPaymentConsumer.java) are designed to handle asynchronous processing, where the underlying execution strategy can be toggled to optimize for scalability.

### 📚 Resources
- [Refactoring Guru - Strategy Pattern](https://refactoring.guru/design-patterns/strategy)
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)

---

## 3. 🗄️ Repository Pattern

### Intent
To mediate between the domain and data mapping layers using a collection-like interface for accessing domain objects.

### Implementation
Found in the `hotel-persistence` module:
- [`HotelRepository`](hotel-persistence/src/main/java/com/fran/hotel/persistence/repository/HotelRepository.java), [`ReservationRepository`](hotel-persistence/src/main/java/com/fran/hotel/persistence/repository/ReservationRepository.java), etc., extend `JpaRepository`.
- They provide a clean API for the `PersistenceAdapters` to interact with the database, shielding the rest of the application from SQL complexities.

### Academic Perspective
The Repository pattern achieves **Separation of Concerns** by isolating the data access logic. It promotes the use of a **Ubiquitous Language** within the persistence layer, aligning it with the domain's needs.

### 📚 Resources
- [Martin Fowler - Repository Pattern](https://martinfowler.com/eaaCatalog/repository.html)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories)

---

## 4. 🔄 Data Mapper Pattern

### Intent
To move data between objects and a database (or DTOs) while keeping them independent of each other and the mapper itself.

### Implementation: MapStruct
The project uses **MapStruct** (e.g., [`HotelMapper`](hotel-api/src/main/java/com/fran/hotel/api/mapper/HotelMapper.java), [`ReservationEntityMapper`](hotel-persistence/src/main/java/com/fran/hotel/persistence/mapper/ReservationEntityMapper.java)) to transform:
- **Entities** to **Domain Models** (Persistence <-> Domain).
- **Domain Models** to **DTOs** (Domain <-> API).

### Academic Perspective
Unlike the Active Record pattern, Data Mapper ensures that domain objects don't need to know how they are persisted. This keeps the domain models "POJOs" (Plain Old Java Objects), adhering to the **Single Responsibility Principle**.

### 📚 Resources
- [Martin Fowler - Data Mapper](https://martinfowler.com/eaaCatalog/dataMapper.html)
- [MapStruct Official Site](https://mapstruct.org/)

---

## 5. 🛠️ Builder Pattern

### Intent
To separate the construction of a complex object from its representation, allowing the same construction process to create different representations.

### Implementation: Lombok @Builder
Most domain models and DTOs use `@Builder`.
```java
Reservation reservation = Reservation.builder()
    .id(UUID.randomUUID())
    .status(ReservationStatus.PENDING)
    .build();
```
(See [`Reservation.java`](hotel-domain/src/main/java/com/fran/hotel/domain/model/Reservation.java) and [`ReservationStatus.java`](hotel-domain/src/main/java/com/fran/hotel/domain/model/ReservationStatus.java))

### Academic Perspective
The Builder pattern is crucial for maintaining **Immutability**. By providing a clear, fluent API for object creation, it avoids "telescoping constructors" and ensures that objects are in a valid state upon creation.

### 📚 Resources
- [Refactoring Guru - Builder Pattern](https://refactoring.guru/design-patterns/builder)
- [Project Lombok - @Builder](https://projectlombok.org/features/Builder)

---

## 6. 🛡️ Proxy Pattern (AOP)

### Intent
To provide a surrogate or placeholder for another object to control access to it or add behavior.

### Implementation: Spring AOP
Referenced in [`pom.xml`](pom.xml) (`spring-boot-starter-aop`), the project uses Aspect-Oriented Programming (AOP) for cross-cutting concerns:
- **Transaction Management**: `@Transactional` creates proxies to manage database sessions.
- **Validation**: `@Valid` proxies method calls to ensure data integrity before execution.

### Academic Perspective
AOP allows for the **Modularization of Cross-cutting Concerns**. It prevents "code tangling" where business logic is obscured by repetitive technical code like logging or transaction handling.

### 📚 Resources
- [Spring AOP Documentation](https://docs.spring.io/spring-framework/reference/core/aop.html)
- [Baeldung - Introduction to Spring AOP](https://www.baeldung.com/spring-aop)

---

## 7. 🏗️ Factory / Assembler Pattern

### Intent
To centralize the logic of creating or transforming complex objects.

### Implementation
The `hotel-application` module contains "Assemblers" (e.g., [`ReservationAssembler`](hotel-application/src/main/java/com/fran/hotel/application/assembler/ReservationAssembler.java)) that combine multiple sources of data or perform complex transformations that go beyond simple field mapping.

### Academic Perspective
This pattern helps in maintaining the **Dry (Don't Repeat Yourself)** principle by encapsulating complex instantiation logic that might otherwise be scattered across the codebase.

### 📚 Resources
- [Martin Fowler - Data Transfer Object (Assembler mentions)](https://martinfowler.com/eaaCatalog/dataTransferObject.html)
