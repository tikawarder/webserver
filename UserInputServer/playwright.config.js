const { defineConfig } = require('@playwright/test');

const isCI = !!process.env.CI;

module.exports = defineConfig({
  testDir: './e2e',
  workers: isCI ? 4 : 1,
  retries: isCI ? 2 : 1,
  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:9080',
    headless: isCI ? true : false,
    launchOptions: {
      slowMo: isCI ? 0 : 500,
    },
    screenshot: 'only-on-failure',
  },
  projects: [
    {
      name: 'smoke',
      testMatch: 'e2e/smoke.spec.js',
      use: { browserName: 'chromium' },
    },
    {
      name: 'full',
      testMatch: ['e2e/auth.spec.js', 'e2e/person-flow.spec.js', 'e2e/validation.spec.js'],
      use: { browserName: 'chromium' },
      dependencies: ['smoke'],
    },
  ],
});
