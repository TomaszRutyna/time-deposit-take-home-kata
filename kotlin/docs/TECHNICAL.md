# Technical Documentation — Time Deposit Application

## Project Background

This application is a **Time Deposit Management System** for XA Bank, developed as a take-home kata exercise. It manages fixed-term bank deposits with plan-specific monthly interest calculation. The system supports creating and updating time deposits, tracking withdrawals, and automatically recalculating interest on a nightly schedule.

### Business Domain

Customers hold **time deposits** characterized by a plan type, initial balance, and duration. The system supports three plan types with distinct interest rules:

| Plan Type | Annual Rate | Interest Starts After | Interest Stops After |
|-----------|-------------|----------------------|---------------------|
| Basic     | 1%          | 30 days              | Never               |
| Student   | 3%          | 30 days              | 366 days            |
| Premium   | 5%          | 45 days              | Never               |

Interest is calculated monthly using the formula: `balance × rate / 12`, rounded to 2 decimal places.

---

## Technology Stack

| Category          | Technology                              |
|-------------------|-----------------------------------------|
| Language          | Kotlin 2.2.0 (JVM 17)                  |
| Framework         | Spring Boot 4.0.7                       |
| Build Tool        | Apache Maven                            |
| Web Layer         | Spring WebMVC                           |
| Persistence       | Spring Data JPA + Hibernate             |
| Database          | PostgreSQL                              |
| Migrations        | Liquibase                               |
| API Contract      | OpenAPI 3.0 + OpenAPI Generator (kotlin-spring, delegate pattern) |
| API Documentation | springdoc-openapi 3.0.2 (Swagger UI)    |
| Scheduling        | Spring `@Scheduled` + ShedLock 6.3.1    |
| Serialization     | Jackson (Kotlin module + JSR-310)       |
| Testing           | JUnit 5, AssertJ, Spring Boot Test, MockMvc, Testcontainers |
| Containerization  | Docker Compose (PostgreSQL)             |

---

## Architecture

The project follows **Hexagonal Architecture** (Ports & Adapters), ensuring clean separation between business logic and infrastructure concerns.

### Architecture Diagram

```mermaid
graph TB
    subgraph "Driving Adapters (Input)"
        REST["REST Controller<br/>(OpenAPI generated interface)"]
        SCHED["Interest Recalculation<br/>Scheduler (Cron)"]
    end

    subgraph "Application Core"
        subgraph "Ports (In)"
            UP["UpsertTimeDeposit"]
            FT["FetchTimeDeposit"]
            IR["InterestRecalculation"]
        end

        subgraph "Application Services"
            CMD["TimeDepositCommandService"]
            QRY["TimeDepositQueryService"]
        end

        subgraph "Domain"
            CALC["TimeDepositCalculator"]
            PLAN["PlanDefinitionResolver"]
            BASE["BasePlanDefinition"]
            MODEL["TimeDeposit / Withdrawal"]
        end

        subgraph "Ports (Out)"
            REPO_PORT["TimeDepositRepository"]
        end
    end

    subgraph "Driven Adapters (Output)"
        DB_REPO["TimeDepositDbRepository"]
        JPA["JPA Repositories"]
        DB[(PostgreSQL)]
    end

    REST --> UP
    REST --> FT
    SCHED --> IR

    UP --> CMD
    FT --> QRY
    IR --> CMD

    CMD --> CALC
    CMD --> PLAN
    CMD --> REPO_PORT
    QRY --> REPO_PORT

    CALC --> BASE
    CALC --> PLAN

    REPO_PORT --> DB_REPO
    DB_REPO --> JPA
    JPA --> DB
```

### Package Structure

```mermaid
graph LR
    subgraph "org.ikigaidigital"
        A["bootstrap/"] --> B["Application + Config"]
        C["domain/"] --> D["deposit/ (Calculator, Model)"]
        C --> E["plan/ (PlanDefinition, Resolver)"]
        F["port/"] --> G["in/ (Use Case Interfaces)"]
        F --> H["out/ (Repository Interface)"]
        I["application/"] --> J["service/ (Command + Query)"]
        K["adapter/"] --> L["in/rest/ (Controller, Mappers)"]
        K --> M["in/scheduler/ (Cron Job)"]
        K --> N["out/persistence/ (JPA Entities, Repos)"]
    end
```

---

## Main Flows

### 1. Create Time Deposit

```mermaid
sequenceDiagram
    participant Client
    participant Controller as REST Controller
    participant Service as CommandService
    participant Resolver as PlanDefinitionResolver
    participant Repo as Repository
    participant DB as PostgreSQL

    Client->>Controller: PUT /time-deposit (no id)
    Controller->>Service: upsert(TimeDeposit)
    Service->>Resolver: resolve(planType)
    Resolver-->>Service: PlanDefinition (rate, constraints)
    Service->>Service: Calculate nextInterestCalculationDate
    Service->>Repo: save(TimeDeposit)
    Repo->>DB: INSERT time_deposits
    DB-->>Repo: Saved entity
    Repo-->>Service: TimeDeposit
    Service-->>Controller: TimeDeposit
    Controller-->>Client: 200 OK (TimeDepositResponse)
```

### 2. Update Time Deposit (with Withdrawal)

```mermaid
sequenceDiagram
    participant Client
    participant Controller as REST Controller
    participant Service as CommandService
    participant Repo as Repository
    participant DB as PostgreSQL

    Client->>Controller: PUT /time-deposit (with id, lower amount)
    Controller->>Service: upsert(TimeDeposit)
    Service->>Repo: findById(id)
    Repo->>DB: SELECT
    DB-->>Repo: Existing deposit
    Repo-->>Service: TimeDeposit (old balance)
    Service->>Service: Detect balance decrease
    Service->>Service: Create Withdrawal (oldBalance - newBalance)
    Service->>Repo: save(TimeDeposit + Withdrawal)
    Repo->>DB: UPDATE + INSERT withdrawal
    DB-->>Repo: Saved entities
    Repo-->>Service: TimeDeposit
    Service-->>Controller: TimeDeposit (with withdrawals)
    Controller-->>Client: 200 OK (TimeDepositResponse)
```

### 3. Interest Recalculation (Scheduled Job)

```mermaid
sequenceDiagram
    participant Scheduler as Cron Scheduler
    participant Lock as ShedLock
    participant Service as CommandService
    participant Calculator as TimeDepositCalculator
    participant Resolver as PlanDefinitionResolver
    participant Repo as Repository
    participant DB as PostgreSQL

    Scheduler->>Lock: Acquire lock
    Lock-->>Scheduler: Lock granted
    Scheduler->>Service: recalculateInterests()
    
    loop Batch (page size 50)
        Service->>Repo: findEligibleDeposits(today, page)
        Repo->>DB: SELECT WHERE nextInterestCalcDate <= today
        DB-->>Repo: Deposits batch
        Repo-->>Service: List<TimeDeposit>
        
        loop Each deposit
            Service->>Calculator: updateBalance(deposit)
            Calculator->>Resolver: resolve(planType)
            Resolver-->>Calculator: PlanDefinition
            Calculator->>Calculator: interest = balance × rate / 12
            Calculator->>Calculator: balance += interest
            Calculator->>Calculator: Set next calculation date
            Calculator-->>Service: Updated deposit
            Service->>Repo: save(deposit)
            Repo->>DB: UPDATE
        end
    end
    
    Scheduler->>Lock: Release lock
```

### 4. Fetch Time Deposits (Paginated)

```mermaid
sequenceDiagram
    participant Client
    participant Controller as REST Controller
    participant Service as QueryService
    participant Repo as Repository
    participant DB as PostgreSQL

    Client->>Controller: GET /time-deposit?page=0&size=10
    Controller->>Service: fetch(pageable)
    Service->>Repo: findAll(pageable)
    Repo->>DB: SELECT with LIMIT/OFFSET
    DB-->>Repo: Page of deposits
    Repo-->>Service: Page<TimeDeposit>
    Service-->>Controller: Page<TimeDeposit>
    Controller-->>Client: 200 OK (TimeDepositPage)
```

---

## Database Schema

```mermaid
erDiagram
    TIME_DEPOSITS {
        bigint id PK
        varchar plan_type
        numeric balance
        integer day_of_deposit
        date for_date
        date last_interest_calculation_date
        date next_interest_calculation_date
        integer version
        timestamp created_at
        timestamp updated_at
    }

    WITHDRAWALS {
        bigint id PK
        numeric amount
        date date
        bigint time_deposit_id FK
        timestamp created_at
    }

    SHEDLOCK {
        varchar name PK
        timestamp lock_until
        timestamp locked_at
        varchar locked_by
    }

    TIME_DEPOSITS ||--o{ WITHDRAWALS : "has many"
```

---

## API Specification

The REST API is defined using **OpenAPI 3.0** specification (`src/main/resources/openapi/time-deposit-api.yml`). Code generation produces:
- Controller interface (`TimeDepositApi`)
- Request/Response DTOs (`TimeDepositRequest`, `TimeDepositResponse`, `TimeDepositPage`)

Swagger UI is available at runtime: `http://localhost:8080/swagger-ui/index.html`

---

## Configuration

Plan definitions and interest rules are externalized in `application.yaml`, making them easy to modify without code changes:

```yaml
time-deposit:
  plans:
    basic:    { interest-rate: 0.01, first-interest-calculation-day: 30 }
    student:  { interest-rate: 0.03, first-interest-calculation-day: 30, last-interest-calculation-day: 366 }
    premium:  { interest-rate: 0.05, first-interest-calculation-day: 45 }
```

The interest recalculation schedule is also configurable:
```yaml
interest-recalculation:
  cron: '0 0 0 * * *'   # daily at midnight
```
