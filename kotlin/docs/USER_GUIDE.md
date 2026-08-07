# User Guide — Time Deposit Application

## Prerequisites

Before running the application, ensure you have the following installed:

- **JDK 17** or higher
- **Apache Maven 3.x**
- **Docker** and **Docker Compose**

---

## Step 1: Start the Database

The application requires a PostgreSQL database. Use the provided `docker-compose.yml` to start it:

```bash
cd kotlin
docker compose up -d
```

This starts a PostgreSQL container with the following configuration:
- **Host:** localhost
- **Port:** 5432
- **Database:** time-deposit-db
- **Username:** user
- **Password:** pass

To verify the database is running:

```bash
docker compose ps
```

To view database logs:

```bash
docker compose logs postgres
```

To stop the database:

```bash
docker compose down
```

---

## Step 2: Build the Application

Build the project using Maven. This step compiles Kotlin code, generates API interfaces from the OpenAPI specification, and runs all tests:

```bash
mvn clean package
```

To skip tests during build:

```bash
mvn clean package -DskipTests
```

---

## Step 3: Run the Application

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

The application starts on **port 8080** by default.

Once started, you can access:
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI Spec:** http://localhost:8080/v3/api-docs

---

## Step 4: Run Tests

Tests use Testcontainers to spin up a dedicated PostgreSQL instance, so Docker must be running:

```bash
mvn test
```

---

## API Endpoints

### Create a Time Deposit

Creates a new time deposit (omit the `id` field):

```bash
curl -X PUT http://localhost:8080/time-deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000.00,
    "days": 365,
    "planType": "basic"
  }'
```

**Expected response (201/200):**

```json
{
  "id": 1,
  "amount": 10000.00,
  "days": 365,
  "planType": "basic",
  "withdrawals": []
}
```

### Create a Student Plan Deposit

```bash
curl -X PUT http://localhost:8080/time-deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00,
    "days": 180,
    "planType": "student"
  }'
```

**Expected response:**

```json
{
  "id": 2,
  "amount": 5000.00,
  "days": 180,
  "planType": "student",
  "withdrawals": []
}
```

### Create a Premium Plan Deposit

```bash
curl -X PUT http://localhost:8080/time-deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000.00,
    "days": 730,
    "planType": "premium"
  }'
```

**Expected response:**

```json
{
  "id": 3,
  "amount": 50000.00,
  "days": 730,
  "planType": "premium",
  "withdrawals": []
}
```

### Update a Time Deposit (Partial Withdrawal)

Update an existing deposit by providing the `id`. If the amount is reduced, a withdrawal is automatically recorded:

```bash
curl -X PUT http://localhost:8080/time-deposit \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "amount": 8000.00,
    "days": 365,
    "planType": "basic"
  }'
```

**Expected response (withdrawal of 2000.00 recorded):**

```json
{
  "id": 1,
  "amount": 8000.00,
  "days": 365,
  "planType": "basic",
  "withdrawals": [
    {
      "id": 1,
      "amount": 2000.00,
      "date": "2026-08-07"
    }
  ]
}
```

### List Time Deposits (Paginated)

Retrieve all time deposits with pagination:

```bash
curl -X GET "http://localhost:8080/time-deposit?page=0&size=10"
```

**Expected response:**

```json
{
  "number": 0,
  "size": 10,
  "content": [
    {
      "id": 1,
      "amount": 8000.00,
      "days": 365,
      "planType": "basic",
      "withdrawals": [
        {
          "id": 1,
          "amount": 2000.00,
          "date": "2026-08-07"
        }
      ]
    },
    {
      "id": 2,
      "amount": 5000.00,
      "days": 180,
      "planType": "student",
      "withdrawals": []
    }
  ]
}
```

### Custom Page Size

```bash
curl -X GET "http://localhost:8080/time-deposit?page=0&size=5"
```

### Second Page

```bash
curl -X GET "http://localhost:8080/time-deposit?page=1&size=5"
```

---

## Troubleshooting

### Database Connection Refused

If you see `Connection refused` errors, ensure the PostgreSQL container is running:

```bash
docker compose ps
docker compose up -d
```

### Port 5432 Already in Use

If port 5432 is occupied by another PostgreSQL instance:

```bash
docker compose down
# Stop the conflicting service, then:
docker compose up -d
```

### Port 8080 Already in Use

If another application is using port 8080, you can override the port:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

### Tests Fail with Docker Errors

Integration tests require Docker to be running (for Testcontainers). Ensure Docker daemon is active:

```bash
docker info
```

### Build Fails at Code Generation

The OpenAPI code generation step requires the API spec file. Ensure `src/main/resources/openapi/time-deposit-api.yml` exists and is valid.
