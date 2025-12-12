// src/App.js (A JAVÍTOTT TARTALOM)

import React from 'react';
// 1. Importáljuk a két komponensünket
import UserList from './components/UserList';
import InputForm from './components/InputForm';

function App() {
  return (
    // A React komponens visszaadja a JSX-et (a tényleges megjelenítendő elemeket)
    <div style={{
        padding: '20px',
        fontFamily: 'Arial, sans-serif',
        // CSS Grid használata a 2 oszlopos elrendezéshez
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '40px',
        maxWidth: '1200px',
        margin: '0 auto'
    }}>

      {/* Bal Oldali Panel: Az Űrlap */}
      <div style={{ borderRight: '1px solid #ccc', paddingRight: '40px' }}>
        <h2>Új felhasználó felvitele</h2>
        <InputForm />
      </div>

      {/* Jobb Oldali Panel: A Lista */}
      <div>
        <h2>Felhasználók az Adatbázisból</h2>
        <UserList />
      </div>

    </div>
  );
}

export default App;