# 🚀 GKE Multi-Service Deployment: Price API

This repository contains the **Price API**, a Spring Boot service following DDD principles. It is deployed to a shared Google Kubernetes Engine (GKE) cluster alongside other microservices using a centralized CI/CD pipeline.

## 🏗 Multi-Repo Architecture Situation

When deploying this service to an existing GCP infrastructure, we maintain a **Shared Cluster** model. This requires specific configuration to ensure that different GitHub repositories can securely share the same Google Cloud Service Account.

### 🔑 The Multi-Repo "Handshake" (WIF)
Even when using a shared Service Account, Google Cloud requires explicit permission for **every new repository**.
* **The Issue:** The new repository initially received a `403 Permission Denied` when trying to generate an access token.
* **The Solution:** We extended the Workload Identity Federation (WIF) trust policy to include this specific repository string.

**Command executed to authorize this repo:**
```bash
gcloud iam service-accounts add-iam-policy-binding 123456789-compute@developer.gserviceaccount.com \
    --project="project-123456789" \
    --role="roles/iam.workloadIdentityUser" \
    --member="principalSet://[iam.googleapis.com/projects/123456789/locations/global/workloadIdentityPools/github-pool/attribute.repository/github-user/github-repo](https://iam.googleapis.com/projects/123456789/locations/global/workloadIdentityPools/github-pool/attribute.repository/github-user/github-repo)"
```

## 🔄 Migration to Spring WebFlux

The application has been migrated from Spring MVC to **Spring WebFlux** to support reactive programming and non-blocking I/O.

### 🛠 Key Changes

1.  **Dependencies**:
    *   Replaced `spring-boot-starter-web` with `spring-boot-starter-webflux`.
    *   Added `reactor-test` for reactive testing support.
    *   Replaced `spring-boot-starter-data-jpa` with `spring-boot-starter-data-r2dbc`.
    *   Replaced `h2` with `r2dbc-h2` for reactive database access.

2.  **Domain Layer**:
    *   Updated `PriceUseCase` and `PricePersistencePort` to return `Mono<Price>` instead of `Optional<Price>`.

3.  **Application Layer**:
    *   Updated `PriceService` to implement the reactive `PriceUseCase`.

4.  **Persistence Layer**:
    *   Updated `PriceEntity` to use Spring Data R2DBC annotations (`@Table`, `@Id`, `@Column`) instead of JPA annotations.
    *   Updated `PriceJpaRepository` to extend `ReactiveCrudRepository` and return `Mono<PriceEntity>`.
    *   Updated `PricePersistenceAdapter` to use the reactive repository and return `Mono<Price>`.

5.  **API Layer**:
    *   Updated `PriceController` to return `Mono<PriceResponse>` and use reactive operators (`map`, `switchIfEmpty`).
    *   Updated `PricesApplication` to use `@EnableR2dbcRepositories` instead of `@EnableJpaRepositories`.

6.  **Testing**:
    *   Updated `TasksApplicationIntegrationTest` to use `WebTestClient` instead of `RestAssured` for reactive integration testing.
    *   Updated `TestContainerConfiguration` to use R2DBC H2 configuration.

## 🧪 Performance Testing with JMeter

This project includes a JMeter test plan to verify the performance of the Price API under load.

### 📋 Prerequisites
*   **Java 21** or higher
*   **Maven**
*   **JMeter 5.5** or higher (optional, for GUI mode)

### 🚀 Running the Test Locally

1.  **Start the Application:**
    You can start the application using Maven or Docker.

    *   **Using Maven:**
        ```bash
        mvn clean package
        java -jar price-api/target/price-api-1.1-SNAPSHOT.jar
        ```

    *   **Using Docker Compose:**
        ```bash
        docker-compose up --build
        ```

    Ensure the application is running at `http://localhost:8080`.

2.  **Run JMeter Test:**
    You can run the test using the JMeter GUI or CLI.

    *   **CLI Mode (Recommended for Load Testing):**
        Navigate to the project root and run:
        ```bash
        jmeter -n -t jmeter/test-plans/price-api-test.jmx -l jmeter/results/results.jtl
        ```
        *   `-n`: Non-GUI mode
        *   `-t`: Path to the test plan
        *   `-l`: Path to save the results

    *   **GUI Mode:**
        Open JMeter, load `jmeter/test-plans/price-api-test.jmx`, and click the Start button.

### 📊 Viewing Results
After running the test in CLI mode, you can generate a dashboard report:

```bash
jmeter -g jmeter/results/results.jtl -o jmeter/dashboard/
```
Open `jmeter/dashboard/index.html` in your browser to view the detailed report.

### 🤖 GitHub Actions
The performance test is automatically executed on every push to `main` via the `JMeter Performance Test` workflow. Artifacts (results and logs) are available for download in the Actions tab.
