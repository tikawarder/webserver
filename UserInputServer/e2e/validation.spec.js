const { test, expect, request } = require('@playwright/test');
const { loginWithKeycloak, getAccessToken } = require('./helpers');

let createdPersonId = null;

test.beforeEach(async ({ page }) => {
  await loginWithKeycloak(page);
});

test.afterEach(async ({ page }) => {
  if (createdPersonId) {
    const token = await getAccessToken(page);
    const apiContext = await request.newContext({ baseURL: 'http://localhost:9081' });
    await apiContext.delete(`/api/persons/${createdPersonId}`, {
      headers: { Authorization: 'Bearer ' + token }
    });
    createdPersonId = null;
  }
});

test('submitting empty form shows validation errors', async ({ page }) => {
  await page.getByTestId('save-button').click();
  await expect(page.locator('[style*="color: red"]').first()).toBeVisible({ timeout: 3000 });
});

test('valid person data is accepted and form resets on success', async ({ page }) => {
  const name = `Valid Test ${Date.now()}`;
  await page.getByTestId('name-input').fill(name);
  await page.getByTestId('birthday-input').fill('1985-03-20');
  await page.getByTestId('city-input').fill('Debrecen');
  await page.getByTestId('save-button').click();

  await expect(page.getByTestId('name-input')).toHaveValue('', { timeout: 10000 });
  await expect(page.getByTestId('form-error')).not.toBeVisible();

  const token = await getAccessToken(page);
  const apiContext = await request.newContext({ baseURL: 'http://localhost:9081' });
  const listResponse = await apiContext.get('/api/persons?size=100&sort=name,asc', {
    headers: { Authorization: 'Bearer ' + token }
  });
  const data = await listResponse.json();
  const created = data.content.find(p => p.name === name);
  if (created) createdPersonId = created.id;
});

test('DELETE removes the person from the database', async ({ page }) => {
  const token = await getAccessToken(page);
  const apiContext = await request.newContext({ baseURL: 'http://localhost:9081' });

  const createResponse = await apiContext.post('/api/persons', {
    data: { name: `Delete Test ${Date.now()}`, birthDay: '1990-01-01', city: 'Pécs' },
    headers: {
      Authorization: 'Bearer ' + token,
      'Content-Type': 'application/json'
    }
  });
  expect(createResponse.status()).toBe(200);
  const created = await createResponse.json();

  const deleteResponse = await apiContext.delete(`/api/persons/${created.id}`, {
    headers: { Authorization: 'Bearer ' + token }
  });
  expect(deleteResponse.status()).toBe(204);

  const listResponse = await apiContext.get('/api/persons?size=100', {
    headers: { Authorization: 'Bearer ' + token }
  });
  const data = await listResponse.json();
  const found = data.content.find(p => p.id === created.id);
  expect(found).toBeUndefined();
});
