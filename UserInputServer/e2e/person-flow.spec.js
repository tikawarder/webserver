const { test, expect } = require('@playwright/test');

test('login, add a person, verify it appears in the list', async ({ page }) => {
  // 1. Navigate to the app
  await page.goto('/');

  // 2. Login with admin credentials
  await page.fill('#username', 'admin');
  await page.fill('#password', 'password');
  await page.click('button[type="submit"]');

  // Wait until login is confirmed
  await expect(page.locator('text=Login successful')).toBeVisible();

  // 3. Fill in the person form
  const testName = 'Playwright Tesztelő';
  await page.fill('input[name="name"]', testName);
  await page.fill('input[name="birthDay"]', '1990-06-15');
  await page.fill('input[name="city"]', 'Budapest');

  // 4. Submit the form
  await page.click('button:has-text("Saving data")');

  // 5. Verify the new person appears in the list
  await expect(page.locator(`text=${testName}`)).toBeVisible({ timeout: 5000 });
});
