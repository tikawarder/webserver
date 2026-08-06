#!/bin/sh
set -eu

cat > /usr/share/nginx/html/runtime-config.js <<EOF
window.RUNTIME_CONFIG = {
  KEYCLOAK_URL: "${KEYCLOAK_URL:-}",
  API_URL: "${API_URL:-}"
};
EOF

exec "$@"
