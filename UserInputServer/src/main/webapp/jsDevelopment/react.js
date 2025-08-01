import React from 'https://esm.sh/react@18';
import ReactDOM from 'https://esm.sh/react-dom@18/client.js';
import Welcome from './welcome.js'; // ez legyen ESM-kompatibilis modul!

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(React.createElement(Welcome, { name: 'Java fejlesztő' }));