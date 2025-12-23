import React from 'react';

function UserItem({ id, name, birthDay, city }) {
  return (
    <div className="user-card">
      <strong>ID:</strong> {id}
      <br />
      <strong>Name:</strong> {name}
      <br />
      <strong>Date of Birth:</strong> {birthDay || "Not provided"}
      <br />
      <strong>City:</strong> {city}
    </div>
  );
}

export default UserItem;