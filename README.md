# Loan Origination System (LOS)

A scalable, thread-safe backend for processing loan applications, built with Spring Boot and PostgreSQL.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Setup & Run](#setup--run)
- [API Documentation & cURL Examples](#api-documentation--curl-examples)
- [Testing](#testing)
- [Sample Data](#sample-data)
- [Postman Collection](#postman-collection)
- [Thread-Safety & Concurrency](#thread-safety--concurrency)
- [Troubleshooting](#troubleshooting)
- [Contact](#contact)

## Features
- Submit and process loan applications
- Automated, concurrent loan approval simulation
- Agent-manager hierarchy and assignment
- Mocked notifications (push/SMS)
- Real-time loan status monitoring
- Top customer analytics
- Pagination and filtering

## Architecture

```mermaid
flowchart TD
    A[API Controller] --> B[Service Layer]
    B --> C[Repository]
    C --> D[(Database)]
    B -->|Async| E[Background Processor]
    B --> F[NotificationService (Mock)]
```

## Setup & Run

1. **Clone the repository**
2. **Create a PostgreSQL database**
3. **Configure `src/main/resources/application.properties`**
4. **Run the app**
   ```bash
   mvn spring-boot:run
   ```

## API Documentation & cURL Examples

### 1. Submit Loan Application
```bash
curl -X POST http://localhost:8080/api/v1/loans \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Akshat Sahcan",
    "customerPhone": "9328674769",
    "loanAmount": 5000.0,
    "loanType": "PERSONAL"
}'
```

### 2. Get Loan Status Counts
```bash
curl -X GET http://localhost:8080/api/v1/loans/status-count
```

### 3. Get Loans by Status with Pagination
```bash
curl -X GET "http://localhost:8080/api/v1/loans?status=APPLIED&page=0&size=10"
```

### 4. Check Which Loan is Assigned to Which Agent
```bash
curl -X GET http://localhost:8080/api/v1/loans/assigned
```
- The response will show each loan and its assigned agent (if any).

### 5. Agent Makes a Decision on a Loan
- Only use this for loans in `UNDER_REVIEW` with a non-null `agent`.
```bash
curl -X PUT http://localhost:8080/api/v1/agents/{agentId}/loans/{loanId}/decision \
  -H "Content-Type: application/json" \
  -d '{"decision": "APPROVE"}'
```

### 6. Get Top Customers
```bash
curl -X GET http://localhost:8080/api/v1/customers/top
```

### 7. (Optional) Get Loans by Any Status with Pagination
```bash
curl -X GET "http://localhost:8080/api/v1/loans?status=APPROVED_BY_AGENT&page=0&size=10"
```

## Testing

- **Run all tests:**
  ```bash
  mvn test
  ```
- **Coverage:** Unit and controller tests for all APIs and core services.

## Sample Data

- Preloaded users/agents in `src/main/resources/data.sql`:
  - Managers: `Manager Anil Kumar`, `Manager Priya Sharma`
  - Agents: `Agent Rohan Mehra`, `Agent Sneha Patil`, `Agent Arjun Singh`, `Agent Kavya Nair`

## Postman Collection

- File: `TurnoLOS.postman_collection.json`
- Import into Postman and use the included requests.

## Thread-Safety & Concurrency

- Loan processing uses a thread pool (`AsyncConfig`).
- Loans are marked as `PROCESSING` before being processed to avoid race conditions.
- Only users with `role = 'AGENT'` are assigned to loans.
- Agent assignment only happens when a loan is set to `UNDER_REVIEW` by the background processor.

## Troubleshooting

- **No loans assigned to agents?**
  - Make sure you have users with `role = 'AGENT'` in your database.
  - The background processor randomly sets loans to `UNDER_REVIEW`. For demo/testing, you can force all loans to go to `UNDER_REVIEW` in the code.
  - Use `/api/v1/loans/assigned` to see all loans and their assigned agents.
- **Agent decision API returns error?**
  - Only the assigned agent can approve/reject a loan.
  - Use `/api/v1/loans/assigned` or `/api/v1/loans?status=UNDER_REVIEW...` to find the correct agent/loan IDs.

