# ADR-002: JWT Stored in localStorage for MVP

**Date:** 2026-05-10
**Status:** Accepted (with known trade-offs, revisit for v0.2.0)
**Deciders:** Taskly core team

## Context

The Angular SPA needs to persist the JWT access token across page refreshes. The two main options are `localStorage` and `HttpOnly cookies`. We need to make a pragmatic choice for v0.1.0 MVP while being transparent about the security trade-offs.

## Decision

Store the JWT access token in **`localStorage`** for v0.1.0.

Rationale for MVP:
- Simpler to implement (no cookie configuration, CSRF handling, or SameSite concerns)
- Faster development velocity for a local-only MVP
- The application targets a local development environment, not public internet

## Security Trade-offs (IMPORTANT)

**`localStorage` is vulnerable to XSS attacks.** If an attacker can inject JavaScript into the page, they can read the token from `localStorage` and impersonate the user.

`HttpOnly cookies` are immune to this attack because JavaScript cannot read them.

## Migration Plan (v0.2.0)

Before any public deployment, migrate to `HttpOnly cookies`:
1. Backend: set JWT in a `Set-Cookie` header with `HttpOnly; Secure; SameSite=Strict`
2. Backend: add CSRF protection (Spring Security CSRF token or double-submit cookie pattern)
3. Frontend: remove all `localStorage` token handling; Angular's `HttpClient` will send cookies automatically
4. Angular: no `Authorization: Bearer` header injection needed

## Mitigations Applied in v0.1.0

- Short access token expiry (15 minutes)
- Refresh token mechanism (7 days)
- Content Security Policy headers (planned for v0.2.0)
- No sensitive user data (PII beyond email) stored in the token payload

## Consequences

**Positive:**
- MVP ships faster
- No CSRF complexity in v0.1.0

**Negative:**
- XSS vulnerability if the frontend ever serves user-generated content
- Must not deploy to production without migrating to HttpOnly cookies
