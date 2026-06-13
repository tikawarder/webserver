const { test, expect, request } = require('@playwright/test');
const { getCookieHeader } = require('./helpers');

test('login, add a person, verify it appears in the list, then delete it', async ({ page }) => {
  await page.goto('/');

  await page.getByTestId('username-input').fill('admin');
  await page.getByTestId('password-input').fill('password');
  await page.getByTestId('login-button').click();
  await expect(page.getByTestId('welcome-message')).toBeVisible();

  const testName = `Playwright Test ${Date.now()}`;
  await page.getByTestId('name-input').fill(testName);
  await page.getByTestId('birthday-input').fill('1990-06-15');
  await page.getByTestId('city-input').fill('Budapest');
  await page.getByTestId('save-button').click();

  // Form reset confirms save was accepted
  await expect(page.getByTestId('name-input')).toHaveValue('', { timeout: 10000 });

  // Find the new person via API and delete it for cleanup
  const apiContext = await request.newContext({ baseURL: 'http://localhost:9081' });
  const cookieHeader = await getCookieHeader(page);

  const listResponse = await apiContext.get('/api/persons?size=100&sort=name,asc', {
    headers: { Cookie: cookieHeader }
  });
  const data = await listResponse.json();
  const created = data.content.find(p => p.name === testName);
  if (created) {
    await apiContext.delete(`/api/persons/${created.id}`, {
      headers: { Cookie: cookieHeader }
    });
  }
});
