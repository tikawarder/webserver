// src/components/UserList.js

import React, { useState, useEffect } from 'react';
import UserItem from './UserItem'; // Importáljuk az alkomponenst

function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // API Végpont: /api/persons (ez a proxy-n keresztül a http://localhost:8081/api/persons-ra megy)
    fetch('/api/persons')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP hiba: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        setUsers(data); // A JSON tömb beállítása az állapotba
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []); // A [] biztosítja, hogy csak a komponens betöltésekor fusson le

  if (loading) {
    return <div style={{ color: '#007bff' }}>Felhasználók betöltése a DatabaseServerről...</div>;
  }
  if (error) {
    return <div style={{ color: 'red' }}>Hiba a betöltéskor: {error}</div>;
  }
  if (users.length === 0) {
    return <div>Jelenleg nincsenek felhasználók az adatbázisban.</div>;
  }

  return (
    <div>
      <p style={{ fontWeight: 'bold' }}>Összesen {users.length} felhasználó található:</p>
      {/* A listák renderelése a .map() metódussal */}
      {users.map(user => (
        <UserItem
          key={user.id}
          id={user.id}
          name={user.name}
          birthDay={user.birthDay}
          city={user.city}
        />
      ))}
    </div>
  );
}

export default UserList;