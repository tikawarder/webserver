// Returns all cookies (including HttpOnly) as a Cookie header string
async function getCookieHeader(page) {
  const cookies = await page.context().cookies();
  return cookies.map(c => `${c.name}=${c.value}`).join('; ');
}

module.exports = { getCookieHeader };
