# URL Shortener API

A REST API service for shortening URLs with JWT authentication, click statistics, and custom/auto-generated link codes.

---

## 📋 Prerequisites & Requirements

* **Java:** JDK 27
* **Docker & Docker Compose** (for containerized execution or running PostgreSQL)
* **PostgreSQL:** version 17+ (if running locally without Docker)
* **Ports:**
  * `8080` — API Web Service
  * `5432` — PostgreSQL Database

---

## ⚙️ Environment Variables

Copy the template file to configure your local environment:
```bash
cp .env.example .env
```

| Variable | Description | Default Value | Usage Location |
|---|---|---|---|
| `DB_URL` | JDBC URL for PostgreSQL connection | `jdbc:postgresql://localhost:5432/sbmt` | `application.properties` |
| `DB_USERNAME` | Database username | `postgres` | `application.properties` |
| `DB_PASSWORD` | Database password | `postgres` | `application.properties` |
| `JWT_KEY` (`jwt.key`) | Secret key used to sign and validate JWT tokens | Defined in `application.properties` | Spring Security / Nimbus JWT |
| `POSTGRES_DB` | Database name initialized in the container | `sbmt` | `compose.yaml` |
| `POSTGRES_USER` | PostgreSQL container username | `postgres` | `compose.yaml` |
| `POSTGRES_PASSWORD` | PostgreSQL container password | `postgres` | `compose.yaml` |

---

## 🚀 Getting Started

### Option 1: Run with Docker Compose (Recommended)

Starts both PostgreSQL and the application container:

```bash
docker compose up --build
```

To stop:
```bash
docker compose down
```

---

### Option 2: Run Locally with Gradle

1. **Start PostgreSQL**:
   You can start only the database service using Docker:
   ```bash
   docker compose up postgres -d
   ```
   Or ensure a local PostgreSQL instance is running on port `5432` with database `sbmt` and credentials `postgres`/`postgres`.

2. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

---

## 🧪 Testing & Building

* **Run tests:**
  ```bash
  ./gradlew test
  ```
  *(Tests utilize Testcontainers and require a running Docker daemon)*

* **Build executable artifact (WAR/JAR):**
  ```bash
  ./gradlew bootWar
  ```
  The built artifact will be located in the `build/libs/` directory.

---

## 📖 API Documentation (Swagger / OpenAPI)

Once the application is running, interactive API documentation and testing tools are accessible at:

* **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI Specification (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
