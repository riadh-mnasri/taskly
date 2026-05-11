# ADR-001: Hexagonal Architecture + Domain-Driven Design

**Date:** 2026-05-10
**Status:** Accepted
**Deciders:** Taskly core team

## Context

We need an architecture for a growing task management application targeting middle-school students. The initial scope is simple (auth + tasks), but v0.2.0 will add routines, gamification, parent roles, and notifications. We want the domain logic to be testable in isolation without starting a Spring context or connecting to a database.

## Decision

We adopt **Hexagonal Architecture** (Ports & Adapters) combined with **Domain-Driven Design** principles, organized as a Gradle multi-module monorepo with one module per bounded context.

Key rules:
1. The domain layer has zero framework imports (no Spring, no JPA)
2. Ports (use case interfaces and repository interfaces) live in the domain
3. Adapters (REST controllers, JPA repositories) live in the infrastructure layer
4. Application services implement use case ports and orchestrate domain objects
5. Each bounded context is a separate Gradle module

## Consequences

**Positive:**
- Domain logic is testable with pure JUnit (no Spring context needed)
- Easy to swap adapters — e.g., replace PostgreSQL with MongoDB without touching domain
- Clear boundaries prevent accidental coupling between contexts
- Scales naturally: add a new context = new module

**Negative:**
- More boilerplate than a simple layered architecture (DTOs, mappers, ports)
- Steeper learning curve for developers unfamiliar with hexagonal architecture
- Over-engineering risk for very small features — mitigated by pragmatic application (not every tiny thing needs a port)

## Alternatives Considered

- **Simple layered architecture (Controller → Service → Repository):** Faster to start, but domain logic gets tangled with Spring annotations; harder to test in isolation.
- **CQRS + Event Sourcing:** Too complex for MVP; deferred to future consideration if audit trail becomes a requirement.
