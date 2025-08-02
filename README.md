## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd turno-los
```

### 2. Database Setup

#### Option A: PostgreSQL (Production)
```sql
CREATE DATABASE turno_los;
CREATE USER turno_user WITH PASSWORD 'turno_password';
GRANT ALL PRIVILEGES ON DATABASE turno_los TO turno_user;
```

#### Option B: H2 (Development)
The application is configured to use H2 in-memory database for development by default.

### 3. Configuration
Update `src/main/resources/application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/turno_los
    username: turno_user
    password: turno_password
```

### 4. Build and Run
To run on docker
```bash
# Build a image with name new-img
docker build -t new-img .

# Run the new image
docker run -p 8080:8080 new-img
```
or,
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Base URL
```
http://localhost:8080/api/v1
```

### Endpoints

#### 1. Submit Loan Application
```http
POST /loans
Content-Type: application/json

{
  "customerName": "John Doe",
  "customerPhone": "+1234567890",
  "loanAmount": 50000.00,
  "loanType": "PERSONAL"
}
```

#### 2. Get Loan Status Count
```http
GET /loans/status-count
```

#### 3. Get Loans with Pagination
```http
GET /loans?status=APPLIED&page=0&size=10
```

#### 4. Agent Decision
```http
PUT /agents/{agentId}/loans/{loanId}/decision
Content-Type: application/json

{
  "decision": "APPROVE"
}
```

#### 5. Top Customers
```http
GET /customers/top
```

## Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn jacoco:report
```

## Configuration

### Application Properties
```yaml
# Database
spring.datasource.url: Database connection URL
spring.datasource.username: Database username
spring.datasource.password: Database password

# JPA
spring.jpa.hibernate.ddl-auto: validate
spring.jpa.show-sql: false

# Thread Pool
los.processing.thread-pool-size: 5
los.processing.delay-min-seconds: 20
los.processing.delay-max-seconds: 30

# Notification
los.notification.enabled: true
```