import React, { useState, useEffect } from 'react';

import MainLayout from './components/MainLayout';
import WelcomePanel from './components/WelcomePanel';
import InputForm from './components/InputForm';
import AboutPanel from './components/AboutPanel';
import UserList from './components/UserList/UserList';
import PageCounter from './components/PageCounter';
import Login from './components/Login';

function App() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isAuthenticated, setIsAuthenticated] = useState(null);

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
            setIsAuthenticated(false);
            throw new Error('Please sign in to view and manage users.');
        }
        if (!response.ok) { throw new Error(`HTTP error: ${response.status}`); }
        return response.json();
      })
      .then(data => {
        setUsers(data.content || []);
        setTotalPages(data.totalPages);
        setPage(data.number);
        setLoading(false);
        setIsAuthenticated(prev => prev === null ? true : prev);
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
    fetchUsers(page);
  };

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    fetchUsers(0);
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
  };

  return (
    <MainLayout
        welcomePanel={<WelcomePanel onLoginSuccess={handleLoginSuccess} onLogout={handleLogout} />}
        formPanel={
          isAuthenticated === true
            ? <InputForm onSubmissionSuccess={handleSuccessSubmit} />
            : isAuthenticated === false
              ? <AboutPanel />
              : null
        }
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
