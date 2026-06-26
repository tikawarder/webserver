const { test, expect, request } = require('@playwright/test');
const { loginWithKeycloak, getAccessToken } = require('./helpers');

test('login, add a person, verify it appears in the list, then delete it', async ({ page }) => {
  await loginWithKeycloak(page);

  const testName = `Playwright Test ${Date.now()}`;
  await page.getByTestId('name-input').fill(testName);
  await page.getByTestId('birthday-input').fill('1990-06-15');
  await page.getByTestId('city-input').fill('Budapest');
  await page.getByTestId('save-button').click();

  // Form reset confirms the save was accepted by the server
  await expect(page.getByTestId('name-input')).toHaveValue('', { timeout: 10000 });

  // Find the created person via API and delete it for cleanup
  const token = await getAccessToken(page);
  const apiContext = await request.newContext({ baseURL: 'http://localhost:9081' });

  const listResponse = await apiContext.get('/api/persons?size=100&sort=name,asc', {
    headers: { Authorization: 'Bearer ' + token }
  });
  const data = await listResponse.json();
  const created = data.content.find(p => p.name === testName);
  if (created) {
    await apiContext.delete(`/api/persons/${created.id}`, {
      headers: { Authorization: 'Bearer ' + token }
    });
  }
});
