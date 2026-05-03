# User Management System

A production-deployed Spring Boot REST API with JWT authentication, Role-Based Access Control (RBAC), and PostgreSQL - containerized with Docker, reverse-proxied with Nginx, and deployed to Oracle Cloud Infrastructure (OCI).

---

## Features

- JWT-based authentication (login, token validation)
- Role-Based Access Control (RBAC) with predefined roles (e.g. ADMIN, USER)
- Data initializer for seeding default roles and users on startup
- Integration tests for end-to-end API correctness
- Dockerized application and PostgreSQL database
- Nginx configured as a reverse proxy
- Deployed to Oracle Cloud Infrastructure (OCI) free tier

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot |
| Database | PostgreSQL |
| Auth | JWT (JSON Web Tokens) |
| Containerization | Docker |
| Reverse Proxy | Nginx |
| Cloud | Oracle Cloud Infrastructure (OCI) |

---

## Architecture

```
Client
  │
  ▼
Nginx (Reverse Proxy)
  │
  ▼
Spring Boot Container (Port 8080)
  │
  ▼
PostgreSQL Container
```

---

## API Endpoints

### Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register a new user |
| POST | `/auth/login` | Public | Login and receive JWT token |

### Users (all under `/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/auth/{id}` | Owner or ADMIN | Get user by ID |
| GET | `/auth/email/{email}` | READ_USERS authority | Get user by email |
| PUT | `/auth/{id}` | ADMIN | Update user details |
| DELETE | `/auth/{id}` | ADMIN | Delete user by ID |
| GET | `/auth` | ADMIN | Get paginated users (params: `page`, `size`, `sortBy`) |
| PUT | `/auth/{id}/promote` | ADMIN | Promote user to ADMIN role |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker

### Running Locally (without Docker)

1. Clone the repository
```bash
git clone https://github.com/pekeerthana/Java_Applications.git
cd Java_Applications
```

2. Configure your database in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/usermanagement
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

3. Run the application
```bash
./mvnw spring-boot:run
```

4. API available at `http://localhost:8080`

### Running with Docker Compose (local development)

1. Make sure Docker is running on your machine

2. Build and start both containers (Spring Boot + PostgreSQL)
```bash
docker-compose up --build
```

3. API available at `http://localhost:8080`

4. To stop the containers
```bash
docker-compose down
```

> The `db` service (PostgreSQL) starts first with a healthcheck. The `app` service waits until the database is ready before starting, so no manual sequencing is needed.

### Deploying to OCI (Production)

1. Build the JAR locally
```bash
./mvnw clean package -DskipTests
```

2. Copy the project files to your OCI instance
```bash
scp -i your_private_key -r ./* opc@your_oci_ip:~/app/
```

3. SSH into the OCI instance
```bash
ssh -i your_private_key opc@your_oci_ip
```

4. Navigate to the app directory and start containers
```bash
cd ~/app
docker-compose up --build -d
```

5. Nginx routes incoming traffic on port 80 to the Spring Boot container on port 8080

> Use `docker-compose logs -f` to monitor logs after deployment.

---

## Running Tests

```bash
./mvnw test
```

Integration tests cover:
- **AuthControllerIntegrationTest** — end-to-end tests for registration, login, and JWT token flows
- **UserServiceIntegrationTest** — service-layer tests for user CRUD and role assignment
- **TestDataInitializer** — seeds test-specific roles and users for a consistent test environment

---

## Deployment

The application is deployed on **Oracle Cloud Infrastructure (OCI)** free tier:

- Spring Boot app and PostgreSQL run as **Docker containers** on an OCI VM instance
- **Nginx** is configured as a reverse proxy to route incoming traffic to the Spring Boot container
- Environment variables are managed via the OCI instance configuration

Added **deploy.yml** to automate the CI/CD pipeline whenever pushed to main branch

---

## Project Structure

```
src/
└── main/
    └── java/
        └── com/example/demo/
            ├── config/            # App configuration
            ├── controller/        # REST controllers
            ├── dto/               # Data Transfer Objects
            ├── entity/            # JPA entities
            ├── enums/             # Enums (e.g. roles)
            ├── exception/         # Custom exception handling
            ├── mapper/            # Entity-DTO mappers
            ├── model/             # Request/response models
            ├── repository/        # JPA repositories
            ├── security/          # JWT & Spring Security config
            ├── service/           # Business logic
            ├── DataInitializer.java   # Seeds default roles and users
            └── DemoApplication.java   # Application entry point
```

---

## Author

**Keerthana Peddireddy**
[LinkedIn](https://www.linkedin.com/in/keerthana-reddy-000/) · [GitHub](https://github.com/pekeerthana)
