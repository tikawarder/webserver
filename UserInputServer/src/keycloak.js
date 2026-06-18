import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8180',
  realm: 'webserver-realm',
  clientId: 'react-app',
});

export default keycloak;
