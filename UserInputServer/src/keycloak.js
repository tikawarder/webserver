import Keycloak from 'keycloak-js';

// crypto.randomUUID() (used unconditionally by keycloak-js for state/nonce, not just PKCE) requires a
// secure context (HTTPS/localhost); crypto.getRandomValues() doesn't, so build a UUID from it instead.
if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'undefined' && typeof crypto.getRandomValues === 'function') {
  crypto.randomUUID = function randomUUID() {
    const bytes = crypto.getRandomValues(new Uint8Array(16));
    bytes[6] = (bytes[6] & 0x0f) | 0x40;
    bytes[8] = (bytes[8] & 0x3f) | 0x80;
    const hex = Array.from(bytes, (b) => b.toString(16).padStart(2, '0'));
    return `${hex.slice(0, 4).join('')}-${hex.slice(4, 6).join('')}-${hex.slice(6, 8).join('')}-${hex.slice(8, 10).join('')}-${hex.slice(10, 16).join('')}`;
  };
}

// Set at container start by docker-entrypoint.sh, not build time — REACT_APP_* vars bake in at `npm run build`.
const keycloakUrl = window.RUNTIME_CONFIG?.KEYCLOAK_URL || 'http://localhost:8180';

const keycloak = new Keycloak({
  url: keycloakUrl,
  realm: 'webserver-realm',
  clientId: 'react-app',
});

// Expose on window so Playwright tests can extract the token via page.evaluate()
window.keycloak = keycloak;

export default keycloak;
