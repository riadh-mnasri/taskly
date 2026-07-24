# Development Journal

A running log of decisions, discoveries, and lessons learned.

---

## Template Entry

**Date:** YYYY-MM-DD
**Author:** Your name
**Summary:** One-line summary of what happened.

### What I did
_Describe the work completed._

### Decisions made
_Any decisions that aren't captured in ADRs._

### Obstacles
_What went wrong and how I resolved it._

### Next steps
_What's next._

---

## 2026-05-10: v0.1.0 Bootstrap

**Author:** Taskly team
**Summary:** Initial project scaffolding generated.

### What I did
- Generated complete v0.1.0 project structure
- Set up Gradle multi-module backend (shared-kernel, identity, task-management, application)
- Implemented hexagonal architecture with TDD on domain and application layers
- Set up Angular 18 frontend with Angular Material + Tailwind CSS
- Configured Docker Compose for PostgreSQL + MailHog
- Added Liquibase migrations and seed data

### Decisions made
- JWT in localStorage for MVP (documented in ADR-002)
- Liquibase over Flyway (documented in ADR-003)
- MailHog wired but not used in v0.1.0: placeholder for email features in v0.2.0

### Next steps
- Run full verification checklist
- Fill in screenshot placeholders in README.md
- Begin v0.2.0 planning
