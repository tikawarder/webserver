import React, { useState, useEffect } from 'react';

import MainLayout from './components/MainLayout';
import WelcomePanel from './components/WelcomePanel';
import InputForm from './components/InputForm';
import UserList from './components/UserList';
import PageCounter from './components/PageCounter';
import Welcome from'./learn/Welcome';

function App() {
  // hooks for states
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchUsers = () => {
    setLoading(true);
    setError(null);

    fetch('/api/persons')
      .then(response => {
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
        welcomePanel={<Welcome myFavoriteColor="orange" name ="Tomi" />}
        formPanel={<InputForm onSubmissionSuccess={handleSuccessSubmit} />}

        // 3. Lista panel (Adatokkal)
        // Átadjuk a fetchUsers függvényt "onRefresh" néven
              listPanel={
                <UserList
                    users={users}
                    loading={loading}
                    error={error}
                    onRefresh={fetchUsers}
                />
              }

        // 4. Számláló panel
        counterPanel={<PageCounter />}
    />
  );
}

export default App;