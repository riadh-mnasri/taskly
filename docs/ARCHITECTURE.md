# Taskly Architecture

## C4 Model: Context Diagram

```mermaid
C4Context
    title Taskly — System Context

    Person(student, "Student (11-15)", "A middle-school student managing their tasks")
    System(taskly, "Taskly", "Web application for task management: homework, exams, personal tasks")
    SystemDb(postgres, "PostgreSQL 16", "Persistent storage for users and tasks")
    System_Ext(mailhog, "MailHog (dev)", "Local SMTP sink for email preview")

    Rel(student, taskly, "Uses", "HTTPS / browser")
    Rel(taskly, postgres, "Reads/Writes", "JDBC / JPA")
    Rel(taskly, mailhog, "Sends emails (v0.2.0)", "SMTP")
```

## C4 Model: Container Diagram

```mermaid
C4Container
    title Taskly — Containers

    Person(student, "Student", "Browser user")

    Container(spa, "Angular SPA", "Angular 18, TypeScript", "Single-page app served on port 4200 in dev")
    Container(api, "Spring Boot API", "Kotlin 2.0, Spring Boot 3.3", "REST API on port 8080; JWT auth; hexagonal architecture")
    ContainerDb(db, "PostgreSQL 16", "Relational DB", "Stores users and tasks; managed by Liquibase")

    Rel(student, spa, "Opens", "HTTP :4200")
    Rel(spa, api, "API calls", "REST / JSON over HTTP :8080")
    Rel(api, db, "Reads/Writes", "JDBC :5432")
```

## Backend Module Map

```mermaid
graph TD
    APP[application<br/>Spring Boot entry point] --> IDENTITY[identity<br/>Users & Auth]
    APP --> TASK[task-management<br/>Tasks]
    IDENTITY --> SK[shared-kernel<br/>UserId, etc.]
    TASK --> SK
```

## Bounded Context Map

```
┌─────────────────────────────────────┐
│           Identity Context          │
│  User, Email, Password, UserId      │
│  RegisterUser, AuthenticateUser     │
│  JWT Token generation               │
└────────────────┬────────────────────┘
                 │ UserId (shared kernel)
┌────────────────┴────────────────────┐
│       Task Management Context       │
│  Task, Priority, TaskType, Status   │
│  CreateTask, UpdateTask, DeleteTask │
│  ListTasksForUser, MarkAsDone       │
└─────────────────────────────────────┘
```

## Hexagonal Architecture (per module)

```
                    ┌─────────────────────────────────────┐
                    │              Domain                  │
                    │  model/  event/  exception/          │
                    │  port/inbound/   port/outbound/      │
                    └───────────────┬─────────────────────┘
                                    │ interfaces only
          ┌─────────────────────────┼──────────────────────────┐
          │                         │                          │
┌─────────▼──────────┐   ┌──────────▼───────────┐   ┌─────────▼──────────┐
│  REST Controller   │   │  Application Service  │   │  JPA Adapter       │
│  (inbound adapter) │   │  (orchestrates domain)│   │  (outbound adapter)│
└────────────────────┘   └──────────────────────┘   └────────────────────┘
```

## DDD Glossary

| Term | Definition in Taskly |
|------|---------------------|
| **Aggregate** | `User`, `Task`: transactional boundary, consistency enforced within |
| **Value Object** | `Email`, `Password`, `UserId`, `TaskId`, `Subject`, `Deadline`, `EstimatedDuration`: immutable, identity by value |
| **Domain Event** | `UserRegistered`: signals that something meaningful happened in the domain |
| **Use Case (Port)** | Interface in `domain.port.inbound`, e.g., `RegisterUserUseCase` |
| **Repository (Port)** | Interface in `domain.port.outbound`, e.g., `UserRepository` |
| **Application Service** | Implements a use case port, orchestrates domain objects and calls repository ports |
| **Adapter** | Concrete implementation of a port: REST controller (inbound) or JPA repository (outbound) |
| **Bounded Context** | `identity` and `task-management`: each with its own ubiquitous language |
| **Shared Kernel** | `shared-kernel` module: `UserId` shared between contexts |

## Architecture Decision Records

- [ADR-001: Hexagonal Architecture + DDD](architecture/adr/ADR-001-hexagonal-architecture.md)
- [ADR-002: JWT in localStorage for MVP](architecture/adr/ADR-002-jwt-localstorage.md)
- [ADR-003: Liquibase over Flyway](architecture/adr/ADR-003-liquibase.md)
