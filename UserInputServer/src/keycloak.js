import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8180',
  realm: 'webserver-realm',
  clientId: 'react-app',
});

// Expose on window so Playwright tests can extract the token via page.evaluate()
window.keycloak = keycloak;

export default keycloak;
