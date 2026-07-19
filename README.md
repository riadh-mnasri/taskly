# Taskly

> A task management app designed for middle school students (ages 11-15) to manage their homework, exams, and personal tasks with clarity — and stay motivated through gamification.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=spring&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-18-DD0031?logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)

## Live Demo

- **App:** https://taskly-frontend-brown.vercel.app
- **API:** https://backend-production-a738.up.railway.app (health: `/actuator/health`, docs: `/swagger-ui.html`)

**Demo credentials:**
- Email: `demo@taskly.app`
- Password: `Demo1234!`

## Quick Start (local, 3 commands)

```bash
# 1. Start PostgreSQL and MailHog
docker compose up -d

# 2. Start the backend (port 8081)
cd backend && ./gradlew bootRun

# 3. Start the frontend (port 4201)
cd frontend && npm install && npm start
```

Open http://localhost:4201 in your browser. See [LOCAL_SETUP.md](LOCAL_SETUP.md) for details.

## Features

- **Authentication** — Sign up, sign in, JWT access + refresh tokens
- **Tasks** — Create, edit, delete tasks with title, description, subject, priority, due date, estimated duration, type
- **List View** — Sortable, filterable table with color-coded priority badges (red/orange/green)
- **Kanban View** — Drag & drop columns: To Do / In Progress / Done
- **Calendar View** — Month view of tasks by due date (FullCalendar)
- **Dashboard** — Today's tasks, this week's tasks, urgent items
- **Gamification** — XP and levels for completed tasks, 6 earnable badges (first task, streaks, high-priority wins, no late tasks...), duplicate-reward-proof
- **Statistics** — 7-day completion chart, current streak, weekly XP comparison
- **Email Reminders** — Automated email 24h before a task's deadline (daily scheduled job)
- **Persistence** — PostgreSQL 16 with Liquibase migrations

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Kotlin 2.0, Spring Boot 3.3, Java 17 |
| Database | PostgreSQL 16, Liquibase |
| Auth | JWT (HS256, jjwt) |
| API Docs | Springdoc OpenAPI 3 (Swagger UI) |
| Frontend | Angular 18, TypeScript strict, NgRx Signal Store |
| UI | Angular Material + Tailwind CSS, FullCalendar, Chart.js |
| Local Infra | Docker Compose (PostgreSQL + MailHog) |
| Production | Vercel (frontend) + Railway (backend + PostgreSQL) |

## Architecture

Hexagonal architecture with Domain-Driven Design, organized as a Gradle multi-module monorepo:

```
taskly/
├── backend/
│   ├── shared-kernel/       # Shared value objects (UserId, etc.)
│   ├── identity/            # Bounded context: users & auth
│   ├── task-management/     # Bounded context: tasks
│   ├── gamification/        # Bounded context: XP, levels & badges
│   └── application/         # Spring Boot app entry point, scheduled jobs
├── frontend/                # Angular 18 SPA
└── docker-compose.yml
```

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for C4 diagrams and ADRs.

## API Documentation

With the backend running: http://localhost:8081/swagger-ui.html (or the live API's `/swagger-ui.html` above).

See [docs/API.md](docs/API.md) for endpoint reference.

## Scope

v0.1.0 shipped as a focused MVP; several items originally deferred to v0.2.0 (calendar view, email reminders) have since shipped. See [docs/V0.2.0_BACKLOG.md](docs/V0.2.0_BACKLOG.md) for what's still planned (HttpOnly-cookie auth, recurring tasks, parent dashboard, dark mode...).

## License

MIT — see [LICENSE](LICENSE).

## Author

© 2026 [Riadh MNASRI](mailto:riadh.mnasri@gmail.com)
