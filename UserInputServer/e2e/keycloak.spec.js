/**
 * Keycloak-specific E2E tests.
 *
 * These tests verify the OAuth2/OIDC integration itself, not just the UI:
 * - JWT token structure and claims after login
 * - Role-based access control enforced by the backend (@PreAuthorize)
 *
 * Roles in this realm:
 *   ADMIN  → can GET, POST, DELETE
 *   USER   → can GET, POST (no DELETE)
 *   GUEST  → can GET only
 */
const { test, expect, request } = require('@playwright/test');
const { loginWithKeycloak, getAccessToken } = require('./helpers');

test('Keycloak token is a valid JWT with 3 parts', async ({ page }) => {
  await loginWithKeycloak(page);
  const token = await getAccessToken(page);
  expect(token).toBeTruthy();
  expect(token.split('.')).toHaveLength(3);
});

test('token claims include correct roles for admin user', async ({ page }) => {
  await loginWithKeycloak(page, 'admin', 'password');
  const roles = await page.evaluate(() => {
    const token = window.keycloak?.token;
    if (!token) return null;
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.realm_access?.roles ?? [];
  });
  expect(roles).toContain('ADMIN');
  expect(roles).toContain('USER');
});

test('GUEST role cannot create a person (403)', async ({ page }) => {
  await loginWithKeycloak(page, 'guest', 'password');
  const token = await getAccessToken(page);
  const apiContext = await request.newContext({ baseURL: 'http://localhost:9081' });

  const response = await apiContext.post('/api/persons', {
    data: { name: 'Guest Attempt', birthDay: '1990-01-01', city: 'Miskolc' },
    headers: {
      Authorization: 'Bearer ' + token,
      'Content-Type': 'application/json'
    }
  });
  expect(response.status()).toBe(403);
});

test('USER role cannot delete a person (403)', async ({ page, browser }) => {
  // Create a test person with admin credentials
  await loginWithKeycloak(page, 'admin', 'password');
  const adminToken = await getAccessToken(page);
  const apiContext = await request.newContext({ baseURL: 'http://localhost:9081' });

  const createResponse = await apiContext.post('/api/persons', {
    data: { name: `Role Test ${Date.now()}`, birthDay: '1992-05-10', city: 'Győr' },
    headers: {
      Authorization: 'Bearer ' + adminToken,
      'Content-Type': 'application/json'
    }
  });
  expect(createResponse.status()).toBe(200);
  const created = await createResponse.json();

  // Get a user1 token via a separate browser context (fresh session — no SSO cookie overlap)
  const ctx2 = await browser.newContext();
  const page2 = await ctx2.newPage();
  await loginWithKeycloak(page2, 'user1', 'password');
  const user1Token = await getAccessToken(page2);
  await ctx2.close();

  // user1 (USER role) tries to delete — backend @PreAuthorize("hasRole('ADMIN')") should block it
  const deleteResponse = await apiContext.delete(`/api/persons/${created.id}`, {
    headers: { Authorization: 'Bearer ' + user1Token }
  });
  expect(deleteResponse.status()).toBe(403);

  // Cleanup
  await apiContext.delete(`/api/persons/${created.id}`, {
    headers: { Authorization: 'Bearer ' + adminToken }
  });
});
