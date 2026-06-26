const { test, expect } = require('@playwright/test');
const { loginWithKeycloak } = require('./helpers');

test('app loads and sign-in button is visible', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByTestId('login-button')).toBeVisible();
});

test('clicking sign-in redirects to Keycloak login page', async ({ page }) => {
  await page.goto('/');
  await page.getByTestId('login-button').click();
  await page.waitForURL(/.*:8180.*/);
  await expect(page.locator('#username')).toBeVisible();
});

test('successful login shows welcome message', async ({ page }) => {
  await loginWithKeycloak(page);
  await expect(page.getByTestId('welcome-message')).toContainText('Welcome back, admin');
});
