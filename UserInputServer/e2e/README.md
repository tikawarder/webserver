# E2E Tests — Playwright

One test covering the core user flow: login → add a person → verify it appears in the list.

## Prerequisites

All Docker containers must be running:
```bash
docker compose -f docker/docker-compose.yml up --build
```

## Run

```bash
cd UserInputServer

npm run e2e                     # headless
npx playwright test --headed    # watch in browser
```

## Test: `person-flow.spec.js`

| Step | Action | Assertion |
|------|--------|-----------|
| 1 | Open `localhost:8080` | — |
| 2 | Fill login form, click Login | `"Welcome back, admin"` is visible |
| 3 | Fill person form, click Saving data | — |
| 4 | — | Person's name appears in the list |
