# Testing Strategy

## Goal

No code change should silently break the user-facing flow. Every layer of the stack
has a corresponding test layer that catches regressions at the right level — before
they reach Playwright.

---

## Test pyramid

```
        /\
       /E2E\        ← Playwright: critical user journey only (login → create → verify)
      /------\
     / Integ. \     ← Spring Cloud Contract: catch API changes at service boundaries
    /----------\
   /    Unit    \   ← JUnit (backend, exists), React Testing Library (frontend, planned)
  /--------------\
```

**Rule:** if a bug can be caught at a lower level, it should be. Playwright is the
last line of defence — not the first.

---

## 1. Stable selectors

Current tests use text-based selectors (`button:has-text("Login")`). If the button
label changes, the test breaks — even though the feature still works.

**Fix:** add `data-testid` attributes to interactive React elements:

```jsx
// In the React component
<button data-testid="login-submit">Login</button>
<input data-testid="name-input" name="name" />
<button data-testid="save-person">Saving data</button>
```

```javascript
// In the Playwright test
await page.click('[data-testid="login-submit"]');
await page.fill('[data-testid="name-input"]', testName);
await page.click('[data-testid="save-person"]');
```

This decouples the **visible label** from the **test contract** — either can change
independently without breaking the other.

---

## 2. Test isolation — database state

The current test creates a person named "Playwright Tesztelő". Running the test twice
leaves a stale record that can cause false positives on the second run.

**Fix:** use a unique name per run:

```javascript
const testName = `Playwright Test ${Date.now()}`;
```

Or add a `beforeEach` / `afterEach` hook that cleans up test data via the API.

---

## 3. What each layer tests

| Layer | Tool | What it covers |
|---|---|---|
| Unit (backend) | JUnit | Service logic, validation, edge cases |
| Unit (frontend) | React Testing Library *(planned)* | Component rendering, user interactions |
| Contract | Spring Cloud Contract | API shape between microservices |
| E2E | Playwright | Full user journey through real browser |

Playwright should only test **critical paths** — the flows a real user would notice
if they broke:
- Login / logout
- Add a person → verify it appears in the list
- (future) Role-based access: USER cannot add, ADMIN can

---

## 4. Branch and merge rules

```
feature-branch
    │
    ▼  open PR
microservices  ←── Playwright must be green before merge
    │
PlaywrightE2E  ←── rebase onto microservices regularly (monthly at minimum)
```

- Never merge a feature into `microservices` if it breaks the E2E flow.
- Keep `PlaywrightE2E` in sync with `microservices` via rebase — drift causes
  conflicts and outdated tests.

---

## 5. CI/CD (planned — ci-cd branch)

GitHub Actions pipeline triggered on every PR to `microservices`:

```yaml
steps:
  - docker compose up --build -d
  - wait for healthchecks (all services healthy)
  - npm run e2e
  - docker compose down
```

Until CI is in place, run Playwright manually before merging any change that touches:
- Auth flow (login, logout, JWT cookie)
- Person API (POST /api/persons, GET /api/persons)
- React components: WelcomePanel, InputForm, UserList

---

## 6. Priority order

| # | Action | When |
|---|---|---|
| 1 | Add `data-testid` attributes to React components | next task on this branch |
| 2 | Use unique test data + cleanup in Playwright tests | next task on this branch |
| 3 | Rebase `PlaywrightE2E` onto `microservices` regularly | ongoing |
| 4 | GitHub Actions CI pipeline | ci-cd branch phase |
| 5 | React unit tests (React Testing Library) | after Kubernetes phase |
