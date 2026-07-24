# ADR-003: Liquibase over Flyway for Database Migrations

**Date:** 2026-05-10
**Status:** Accepted
**Deciders:** Taskly core team

## Context

We need a database migration tool to manage the PostgreSQL schema evolution. The two primary options in the Spring Boot ecosystem are Liquibase and Flyway.

## Decision

Use **Liquibase** with YAML changeSets.

## Reasoning

| Criterion | Liquibase | Flyway |
|-----------|-----------|--------|
| ChangeSet format | XML, YAML, JSON, SQL | SQL (native), Java |
| Rollback support | Built-in (when using XML/YAML) | Manual scripts required |
| Preconditions | Built-in | Not available |
| Spring Boot integration | First-class | First-class |
| YAML changeSet readability | Good for structured changes | N/A |

Key reasons for Liquibase:
1. **YAML changeSet format** is more readable and diff-friendly than raw SQL for table definitions
2. **Built-in rollback** support is valuable as the schema evolves rapidly in early versions
3. **Preconditions** allow safe migrations that check state before applying (e.g., "only add column if table exists")
4. Team preference from prior experience

## Consequences

**Positive:**
- Structured change tracking with checksums
- Can generate rollback scripts automatically
- Easier to review table creation than raw SQL in code review

**Negative:**
- YAML Liquibase syntax has a learning curve vs. plain SQL
- Slightly more verbose for simple SQL migrations
- The `databasechangelog` and `databasechangeloglock` tables are added automatically

## Alternatives Considered

- **Flyway with SQL:** Simpler for SQL-heavy teams, but lacks rollback and precondition features.
- **Manual SQL scripts:** No tracking, no checksums; rejected immediately.
- **Hibernate `ddl-auto: create-drop`:** Only for local dev/test, never production; not a migration solution.
