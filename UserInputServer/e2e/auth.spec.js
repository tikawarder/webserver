const { test, expect } = require('@playwright/test');
const { loginWithKeycloak } = require('./helpers');

test('person form is not visible before login', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByTestId('save-button')).not.toBeVisible();
});

test('wrong credentials show an error on the Keycloak login page', async ({ page }) => {
  await page.goto('/');
  await page.getByTestId('login-button').click();
  await page.waitForURL(/.*:8180.*/);
  await page.fill('#username', 'admin');
  await page.fill('#password', 'wrongpassword');
  await page.click('#kc-login');
  // Keycloak shows an inline error — selector covers both Keycloak 20+ (pf-m-error) and older builds
  await expect(page.locator('#input-error, .pf-m-error, [class*="kc-feedback"]').first()).toBeVisible({ timeout: 5000 });
});

test('logout returns to the sign-in screen', async ({ page }) => {
  await loginWithKeycloak(page);
  await page.getByTestId('logout-button').click();
  await expect(page.getByTestId('login-button')).toBeVisible({ timeout: 10000 });
});
