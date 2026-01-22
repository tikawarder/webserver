const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'https://backend-service-801953368913.us-east1.run.app',
      changeOrigin: true,
      logLevel: 'debug',
    })
  );
};