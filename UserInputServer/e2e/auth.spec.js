const { test, expect } = require('@playwright/test');

test('person form is not visible before login', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByTestId('save-button')).not.toBeVisible();
});

test('wrong credentials show an error message', async ({ page }) => {
  await page.goto('/');
  await page.getByTestId('username-input').fill('admin');
  await page.getByTestId('password-input').fill('wrongpassword');
  await page.getByTestId('login-button').click();
  await expect(page.locator('text=Login failed')).toBeVisible();
});
