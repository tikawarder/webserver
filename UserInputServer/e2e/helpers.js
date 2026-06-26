const { expect } = require('@playwright/test');

async function loginWithKeycloak(page, username = 'admin', password = 'password') {
  await page.goto('/');
  await page.getByTestId('login-button').click();
  await page.waitForURL(/.*:8180.*/);
  await page.fill('#username', username);
  await page.fill('#password', password);
  await page.click('#kc-login');
  await page.waitForURL(/.*:9080.*/);
  await expect(page.getByTestId('welcome-message')).toBeVisible({ timeout: 10000 });
}

// Extracts the current Keycloak Bearer token from the browser's JS context.
// Only valid after loginWithKeycloak() has completed.
async function getAccessToken(page) {
  return page.evaluate(() => window.keycloak?.token);
}

module.exports = { loginWithKeycloak, getAccessToken };
