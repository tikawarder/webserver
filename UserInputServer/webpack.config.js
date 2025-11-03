const path = require('path');

module.exports = {
  entry: './src/main/webapp/js/main.js',
  output: {
    path: path.resolve(__dirname, 'src/main/webapp/js/dist'),
    filename: 'bundle.js',
    library: 'MyApp',
    libraryTarget: 'umd'
  },
  resolve: {
    extensions: ['.js']
  },
  mode: 'production' // Minified output
};