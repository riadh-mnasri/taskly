# Local Setup Guide

Complete step-by-step instructions to get Taskly running on your machine from scratch.

## Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| Java | 21+ | `java -version` |
| Docker Desktop | 24+ | `docker --version` |
| Node.js | 20+ | `node --version` |
| npm | 10+ | `npm --version` |

### Install Prerequisites (macOS)

```bash
# Java 21 via SDKMAN (recommended)
curl -s "https://get.sdkman.io" | bash
source ~/.sdkman/bin/sdkman-init.sh
sdk install java 21.0.3-tem

# Docker Desktop
brew install --cask docker

# Node.js via nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
source ~/.nvm/nvm.sh
nvm install 20
nvm use 20
```

## Step 1 — Clone and Verify Structure

```bash
git clone <repo-url> taskly
cd taskly
ls -la
# Should show: backend/ frontend/ docker-compose.yml README.md ...
```

## Step 2 — Start Infrastructure (PostgreSQL + MailHog)

```bash
docker compose up -d
```

Verify:
```bash
docker compose ps
# Both taskly-postgres and taskly-mailhog should show "healthy"

# Test PostgreSQL connection
docker exec taskly-postgres psql -U taskly -d taskly -c '\dt'
```

MailHog web UI (for future email features): http://localhost:8025

## Step 3 — Build and Run the Backend

```bash
cd backend

# First build (downloads dependencies, runs tests)
./gradlew build

# Run (starts on port 8080)
./gradlew bootRun
```

Expected output:
```
Started TasklyApplication in X.XXX seconds
```

Verify:
```bash
curl http://localhost:8081/actuator/health
# {"status":"UP"}

# Liquibase migrations applied
docker exec taskly-postgres psql -U taskly -d taskly -c '\dt'
# Should show: tasks, users, databasechangelog, databasechangeloglock
```

Swagger UI: http://localhost:8081/swagger-ui.html

## Step 4 — Install and Run the Frontend

```bash
# In a new terminal
cd frontend
npm install
npm start
```

Expected output:
```
** Angular Live Development Server is listening on localhost:4201 **
```

Open: http://localhost:4201

## Step 5 — Verify Everything Works

1. Sign in with demo account:
   - Email: `demo@taskly.app`
   - Password: `Demo1234!`

2. You should see the Dashboard with 8 pre-seeded tasks.

3. Navigate to Tasks List and Kanban views.

## Troubleshooting

### Backend fails to start: "Connection refused" to PostgreSQL

```bash
# Check PostgreSQL is running
docker compose ps

# Restart if needed
docker compose down && docker compose up -d

# Check logs
docker compose logs postgres
```

### Port 8080 already in use

```bash
lsof -ti:8080 | xargs kill -9
```

### Port 4200 already in use

```bash
lsof -ti:4200 | xargs kill -9
# Or use a different port:
npm start -- --port 4201
```

### Gradle build fails: wrong Java version

```bash
java -version  # must be 21+
# If using SDKMAN:
sdk use java 21.0.3-tem
```

### Frontend: "Module not found" errors

```bash
rm -rf node_modules package-lock.json
npm install
```

### Reset database (drop all data and re-run migrations)

```bash
docker compose down -v         # removes the named volume
docker compose up -d           # fresh PostgreSQL, migrations re-run on next bootRun
```

## Environment Configuration

Backend config is in `backend/application/src/main/resources/application.yml`.

Key values for local development:
- DB URL: `jdbc:postgresql://localhost:5432/taskly`
- DB User: `taskly` / Password: `taskly_secret`
- JWT Secret: see `application.yml` (dev-only, never commit production secrets)
- Backend port: `8080`
- Frontend dev port: `4200`

## Running Tests

```bash
# Backend tests (unit + integration with Testcontainers)
cd backend && ./gradlew test

# Frontend tests
cd frontend && npm test
```

## Stopping Everything

```bash
# Stop frontend: Ctrl+C in its terminal
# Stop backend: Ctrl+C in its terminal
docker compose down
```
