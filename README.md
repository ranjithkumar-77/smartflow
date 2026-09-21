# SmartFlow 🚀

**SmartFlow** is a full-stack service request and technician matching platform built with **Java, Spring Boot, Spring Security, JWT, JPA/Hibernate, and SQL-compatible databases**.

The platform allows customers to create service requests and automatically matches them with suitable technicians. There is **no admin assignment**. Multiple technicians can receive an offer, and the **first eligible technician to accept the request gets the job**.

---

## 🎯 Project Objective

The main goal of SmartFlow is to automate the process of connecting customers with suitable technicians.

Instead of an administrator manually assigning technicians:

```text
Customer
   ↓
Create Service Request
   ↓
Matching Service
   ↓
Find Suitable Technicians
   ↓
Send Offers
   ↓
First Technician Accepts
   ↓
Job Assigned
   ↓
Technician Starts Job
   ↓
Job Resolved
   ↓
Customer Closes Request
   ↓
Customer Gives Review
   ↓
Technician Rating Updated
```

---

## ✨ Key Features

### 👤 Customer

- Customer registration and login
- JWT-based authentication
- Create service requests
- View own service requests
- Track request status
- View assigned technician
- Close resolved requests
- Submit ratings and reviews
- View profile information
- Share service location

### 🔧 Technician

- Technician registration
- Technician profile management
- Add and manage skills
- Update availability
- Receive service offers
- Accept or reject offers
- View assigned jobs
- Start assigned jobs
- Resolve service requests
- Maintain location information
- Maintain technician rating

### 🤖 Smart Matching

SmartFlow automatically finds suitable technicians using factors such as:

- Required skill
- Technician availability
- Distance
- Technician rating
- Current workload
- Matching score

The system can select suitable technicians and send them service offers.

### ⚡ First-Technician-Wins

Multiple technicians may receive an offer for the same request.

Once one eligible technician accepts:

```text
Technician A → ACCEPTED ✅
Technician B → CANCELLED
Technician C → CANCELLED
```

The request is assigned to the technician who successfully accepts first.

Concurrency protection is used to prevent multiple technicians from being assigned to the same request.

### ⭐ Reviews & Ratings

After a completed service:

1. Customer closes the request.
2. Customer submits a rating and review.
3. Technician's average rating is updated.
4. The rating can be used during future technician matching.

---

## 🏗️ Architecture

```text
                    SmartFlow
                       │
                       ▼
              HTML / CSS / JavaScript
                       │
                       │ HTTP / REST API
                       ▼
              Spring Security + JWT
                       │
                       ▼
                   Controller
                       │
                       ▼
                    Service
                       │
                       ▼
                  Repository
                       │
                       ▼
                 JPA / Hibernate
                       │
                       ▼
                    Database
```

### Backend Layers

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and responses |
| Service | Contains business logic |
| Repository | Handles database operations |
| Entity | Represents database tables |
| DTO | Transfers API request/response data |
| Security | Authentication and authorization |
| Utility | Matching and distance calculations |
| Exception | Global API error handling |

---

## 🔄 Service Request Lifecycle

A service request follows this lifecycle:

```text
OPEN
  ↓
ASSIGNED
  ↓
IN_PROGRESS
  ↓
RESOLVED
  ↓
CLOSED
```

### Status Meaning

| Status | Meaning |
|---|---|
| OPEN | Customer created the request |
| ASSIGNED | Technician accepted the request |
| IN_PROGRESS | Technician started the job |
| RESOLVED | Technician completed the work |
| CLOSED | Customer confirmed completion |

---

## 🧑‍💻 Technology Stack

### Backend

- Java 17
- Spring Boot 4.0.8
- Spring MVC
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- Lombok
- Maven

### Frontend

- HTML5
- CSS3
- JavaScript
- Browser Geolocation API
- OpenStreetMap Nominatim
- Google Maps links

### Database

- H2 for local development
- SQL-compatible database support
- Flyway migration support

### DevOps

- Git
- GitHub
- GitHub Actions
- Docker
- Docker Compose
- Nginx

---

## 🔐 Security

SmartFlow uses **JWT-based authentication**.

Authentication flow:

```text
Login
  ↓
Validate Email & Password
  ↓
Generate JWT
  ↓
Client Stores Token
  ↓
Send Token With API Requests
  ↓
JWT Filter Validates Token
  ↓
Spring Security Authorizes Request
```

Passwords are stored using password hashing rather than plain text.

Role-based authorization is used for different operations:

```text
CUSTOMER
TECHNICIAN
```

---

## 📍 Location & Matching

Technician and customer location information can be used during matching.

SmartFlow uses:

- Latitude
- Longitude
- Distance calculation
- Technician availability
- Skills
- Rating
- Current workload

This helps the system identify technicians who are appropriate for a particular service request.

---

## 📂 Project Structure

```text
smartflow/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── docker.yml
│
├── nginx/
│   └── smartflow.conf
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── smartflow/
│   │   │           └── smartflow/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               ├── service/
│   │   │               └── utils/
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       ├── static/
│   │       ├── application.properties
│   │       └── application-prod.yml
│   │
│   └── test/
│
├── Dockerfile
├── docker-compose.prod.yml
├── pom.xml
├── .env.example
├── .gitignore
└── README.md
```

---

## 🔌 Main API Modules

### User

```text
POST /api/users
POST /api/login
```

### Technician

```text
/api/technicians
/api/technician-skills
```

### Skills

```text
/api/skills
```

### Service Requests

```text
/api/service-requests
```

### Matching

```text
/api/matching
```

### Request Offers

```text
/api/request-offers
```

### Reviews

```text
/api/reviews
```

---

## 🧪 Testing

Run the Maven test suite:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Run the application locally:

```powershell
.\mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

---

## ⚙️ Local Development

### 1. Clone the repository

```bash
git clone https://github.com/ranjithkumar-77/smartflow.git
```

### 2. Enter the project

```bash
cd smartflow
```

### 3. Configure environment variables

Create a local `.env` file based on:

```text
.env.example
```

Never commit real passwords, JWT secrets, or other credentials.

### 4. Run the application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

---

## 🐳 Docker

SmartFlow also includes Docker configuration for deployment.

Build the application:

```bash
docker build -t smartflow .
```

Run using Docker Compose:

```bash
docker compose -f docker-compose.prod.yml up --build
```

---

## 🔄 CI/CD

GitHub Actions is configured to automatically:

- Build the project
- Run tests
- Build the Docker image

Workflow files:

```text
.github/workflows/ci.yml
.github/workflows/docker.yml
```

---

## 🛡️ Concurrency Protection

One important part of SmartFlow is preventing two technicians from accepting the same service request simultaneously.

The backend uses transaction and database-level protection to ensure that only one technician can successfully claim an available request.

Conceptually:

```text
Request = OPEN

Technician A ──┐
               ├── Accept
Technician B ──┘

        ↓

Only ONE technician succeeds

        ↓

Request = ASSIGNED
```

Other offers are then cancelled or made invalid.

---

## 🎓 What This Project Demonstrates

This project demonstrates practical knowledge of:

- Java
- Object-Oriented Programming
- Spring Boot
- REST API development
- Spring Data JPA
- Hibernate
- Spring Security
- JWT authentication
- Role-based authorization
- DTO design
- Database relationships
- Transactions
- Concurrency handling
- Exception handling
- Location-based matching
- Business logic implementation
- Docker
- Git & GitHub
- CI/CD with GitHub Actions
- Frontend-backend integration

---

## 🚀 Future Enhancements

Possible future improvements include:

- Real-time technician notifications
- WebSocket-based offer updates
- Online payment integration
- Email/SMS notifications
- Advanced technician ranking
- Redis caching
- Production database deployment
- Cloud deployment
- Admin analytics dashboard
- AI-assisted service categorization

---

## 👨‍💻 Author

**RanjithKumar**

B.E. Computer Science and Engineering

Interested in:

- Java Backend Development
- Spring Boot
- Full Stack Development
- REST APIs
- Database Development
- Software Engineering

---

## 📌 Project

**SmartFlow – Intelligent Service Request and Workforce Management Platform**

Built as a Java Full Stack project to demonstrate real-world backend architecture, authentication, automated technician matching, concurrency handling, and complete service-request workflow.
