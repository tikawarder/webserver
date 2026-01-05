import React, { useState, useEffect } from 'react';

import MainLayout from './components/MainLayout';
import WelcomePanel from './components/WelcomePanel';
import InputForm from './components/InputForm';
import UserList from './components/UserList';
import PageCounter from './components/PageCounter';

function App() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchUsers = () => {
    setLoading(true);
    setError(null);

fetch('/api/persons', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
      .then(response => {
        if (response.status === 403 || response.status === 401) {
            throw new Error('Access denied. Please login!');
        }
        if (!response.ok) { throw new Error(`HTTP error: ${response.status}`); }
        return response.json();
      })
      .then(data => {
        setUsers(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  };
  // at start, we fetch the users from database only once
  useEffect(() => {
    fetchUsers();
  }, []);

  const handleSuccessSubmit = () => {
    fetchUsers(); // after successfully submit new users we fetch all users
  };

  return (
    <MainLayout
        welcomePanel={<WelcomePanel/>}
        formPanel={<InputForm onSubmissionSuccess={handleSuccessSubmit} />}
        listPanel={
          <UserList
            users={users}
            loading={loading}
            error={error}
            onRefresh={fetchUsers}
          />
         }
        counterPanel={<PageCounter />}
    />
  );
}

export default App;