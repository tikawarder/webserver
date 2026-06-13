const { test, expect } = require('@playwright/test');

test('app loads and login form is visible', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByTestId('username-input')).toBeVisible();
  await expect(page.getByTestId('password-input')).toBeVisible();
  await expect(page.getByTestId('login-button')).toBeVisible();
});

test('login succeeds and welcome message is shown', async ({ page }) => {
  await page.goto('/');
  await page.getByTestId('username-input').fill('admin');
  await page.getByTestId('password-input').fill('password');
  await page.getByTestId('login-button').click();
  await expect(page.getByTestId('welcome-message')).toContainText('Welcome back, admin');
});

test('logout returns to login form', async ({ page }) => {
  await page.goto('/');
  await page.getByTestId('username-input').fill('admin');
  await page.getByTestId('password-input').fill('password');
  await page.getByTestId('login-button').click();
  await expect(page.getByTestId('welcome-message')).toBeVisible();

  await page.getByTestId('logout-button').click();
  await expect(page.getByTestId('login-button')).toBeVisible();
});
