import Keycloak from 'keycloak-js';

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
