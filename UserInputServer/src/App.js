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
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchUsers = (pageNumber = 0) => {
    setLoading(true);
    setError(null);

  fetch('/api/persons?page=' + pageNumber + '&size=5&sort=name,asc', {
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
        setUsers(data.content || []);
        setTotalPages(data.totalPages);
        setPage(data.number);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  };
  useEffect(() => {
    fetchUsers(0);
  }, []);

  const handleSuccessSubmit = () => {
    fetchUsers(0);
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
            onRefresh={() => fetchUsers(0)}
            page={page}
            totalPages={totalPages}
            onPageChange={(newPage) => fetchUsers(newPage)}
          />
         }
        counterPanel={<PageCounter />}
    />
  );
}

export default App;