# Contributing to Taskly

## TDD Workflow (Backend)

All backend features must follow strict Test-Driven Development:

1. **RED**: Write a failing JUnit unit test in the domain layer
2. **GREEN**: Write the minimum production code to make it pass
3. **REFACTOR**: Clean up without breaking tests
4. **RED**: Write a failing Mockito service test
5. **GREEN**: Implement the application service
6. **REFACTOR**
7. Add Testcontainers integration test for the adapter/controller
8. Implement the REST controller and JPA adapter

Never write production code without a failing test first.

## Commit Conventions

Format: `type(scope): description`

| Type | When to use |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `test` | Adding or fixing tests |
| `refactor` | Code improvement without behavior change |
| `docs` | Documentation only |
| `chore` | Build, deps, config |
| `style` | Formatting, no logic change |

Examples:
```
feat(identity): implement user registration use case
test(identity): add unit tests for Email value object
fix(tasks): reject past due dates at task creation
docs(architecture): add ADR-004 for signal store
chore(deps): upgrade Spring Boot to 3.4.0
```

## Branch Naming

```
feat/<short-description>     # feature/feat(scope): ...
fix/<short-description>      # bug fix
test/<short-description>     # test-only changes
docs/<short-description>     # documentation
chore/<short-description>    # maintenance
```

Examples:
```
feat/kanban-drag-drop
fix/jwt-expiry-refresh
test/task-domain-unit-tests
docs/adr-004-signal-store
```

## Code Rules

### Backend (Kotlin)
- Domain layer: **zero** Spring/JPA imports (pure Kotlin/Java only)
- Aggregates: private constructors + static factory methods
- Value Objects: immutable `data class` with validation in `init {}`
- DTOs live in the REST layer: never expose domain models in controllers
- Use `Result<T>` or exceptions (not nullable return types) for error paths
- All public APIs documented with KDoc (one-liner minimum)

### Frontend (Angular/TypeScript)
- Strict TypeScript: no `any` except for truly dynamic data
- Standalone components only (no NgModules)
- Signals for local state, Signal Store for feature state
- Reactive Forms (no template-driven forms)
- Angular Material components: do not reimplement UI primitives
- Services are injected via `inject()`, not constructor parameters

## Test Coverage Targets

| Layer | Target |
|-------|--------|
| Backend domain | 100% |
| Backend application services | ≥ 80% |
| Backend REST controllers | ≥ 70% (integration tests) |
| Frontend services | ≥ 70% |
| Frontend components | ≥ 50% |

## Pull Request Process

1. Branch off `main`
2. Follow TDD: tests must exist before production code in commits
3. `./gradlew build` must pass (tests + compilation)
4. `npm test` must pass
5. Update `docs/JOURNAL.md` with a brief entry
6. Squash commits if the history is messy, keep if it tells a story
7. One reviewer required before merge

## Architecture Rules

- Never bypass the hexagonal boundary: controllers call use cases, not repositories
- No cross-module dependencies except through `shared-kernel`
- New bounded context → new Gradle module (discuss first)
- No `@Autowired` field injection: constructor injection only (or `inject()` in Kotlin)
