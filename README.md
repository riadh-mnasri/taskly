# Taskly

> A task management app designed for middle school students (ages 11-15) to manage their homework, exams, and personal tasks with clarity and focus.

## Quick Start (3 commands)

```bash
# 1. Start PostgreSQL and MailHog
docker compose up -d

# 2. Start the backend (port 8080)
cd backend && ./gradlew bootRun

# 3. Start the frontend (port 4200)
cd frontend && npm install && npm start
```

Open http://localhost:4201 in your browser.

**Demo credentials (pre-seeded):**
- Email: `demo@taskly.app`
- Password: `Demo1234!`

## Features (v0.1.0)

- **Authentication** — Sign up, sign in, JWT access + refresh tokens
- **Tasks** — Create, edit, delete tasks with title, description, subject, priority, due date, estimated duration, type
- **List View** — Sortable, filterable table with color-coded priority badges (red/orange/green)
- **Kanban View** — Drag & drop columns: To Do / In Progress / Done
- **Dashboard** — Today's tasks, this week's tasks, urgent items
- **Persistence** — PostgreSQL 16 with Liquibase migrations

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Kotlin 2.0, Spring Boot 3.3, Java 21 |
| Database | PostgreSQL 16, Liquibase |
| Auth | JWT (HS256, jjwt) |
| API Docs | Springdoc OpenAPI 3 (Swagger UI) |
| Frontend | Angular 18, TypeScript strict |
| UI | Angular Material + Tailwind CSS |
| State | NgRx Signal Store |
| Infrastructure | Docker Compose (DB + MailHog) |

## Architecture

Hexagonal architecture with Domain-Driven Design, organized as a Gradle multi-module monorepo:

```
taskly/
├── backend/
│   ├── shared-kernel/       # Shared value objects (UserId, etc.)
│   ├── identity/            # Bounded context: users & auth
│   ├── task-management/     # Bounded context: tasks
│   └── application/         # Spring Boot app entry point
├── frontend/                # Angular 18 SPA
└── docker-compose.yml
```

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for C4 diagrams and ADRs.

## API Documentation

With the backend running: http://localhost:8081/swagger-ui.html

See [docs/API.md](docs/API.md) for endpoint reference.

## Screenshots

> _Screenshots will be added after first deployment._

## Scope

This is **v0.1.0 MVP**. See [docs/V0.2.0_BACKLOG.md](docs/V0.2.0_BACKLOG.md) for what's coming next.

## License

MIT — see [LICENSE](LICENSE).
