// src/components/UserItem.js

import React from 'react';

// A prop-ok desztrukturálása
function UserItem({ id, name, birthDay, city }) {
  return (
    <div style={{
        border: '1px solid #ddd',
        padding: '10px',
        margin: '10px 0',
        borderRadius: '4px',
        backgroundColor: '#f9f9f9'
    }}>
      <strong>ID:</strong> {id}
      <br />
      <strong>Név:</strong> {name}
      <br />
      <strong>Születési dátum:</strong> {birthDay}
      <br />
      <strong>Város:</strong> {city}
    </div>
  );
}

export default UserItem;