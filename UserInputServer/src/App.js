import React, { useState, useEffect, useCallback, useRef } from 'react';

import keycloak from './keycloak';
import MainLayout from './components/MainLayout';
import WelcomePanel from './components/WelcomePanel';
import InputForm from './components/InputForm';
import AboutPanel from './components/AboutPanel';
import UserList from './components/UserList/UserList';
import RagChatPanel from './components/RagChatPanel';

function App() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [keycloakReady, setKeycloakReady] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  
  // Guard to prevent double initialization in React StrictMode
  const isInitialized = useRef(false);

  useEffect(() => {
    if (isInitialized.current) return;
    isInitialized.current = true;

    keycloak.init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      // crypto.subtle (needed for PKCE) only exists in secure contexts (HTTPS/localhost) — this deployment is plain HTTP on a public IP.
      pkceMethod: window.isSecureContext ? 'S256' : false,
    })
      .then((authenticated) => {
        setKeycloakReady(true);
        setIsAuthenticated(authenticated);
      })
      .catch((err) => {
        console.error('[Keycloak] Init failed:', err);
        setKeycloakReady(true);
      });
  }, []);

  const fetchUsers = useCallback((pageNumber = 0) => {
    if (!keycloak.authenticated) return;

    setLoading(true);
    setError(null);

    keycloak.updateToken(30)
      .then(() => {
        return fetch('/api/persons?page=' + pageNumber + '&size=5&sort=name,asc', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + keycloak.token,
          },
        });
      })
      .then(response => {
        if (response.status === 403) {
          throw new Error('Access denied. You do not have permission for this action.');
        }
        if (response.status === 401) {
          keycloak.login();
          return;
        }
        if (!response.ok) { throw new Error(`HTTP error: ${response.status}`); }
        return response.json();
      })
      .then(data => {
        if (data) {
          setUsers(data.content || []);
          setTotalPages(data.totalPages);
          setPage(data.number);
        }
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      fetchUsers(0);
    }
  }, [isAuthenticated, fetchUsers]);

  const handleSuccessSubmit = () => {
    fetchUsers(page);
  };

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    fetchUsers(0);
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUsers([]);
  };

  if (!keycloakReady) {
    return <div style={{ textAlign: 'center', marginTop: '100px' }}>Initializing security...</div>;
  }

  return (
    <MainLayout
        welcomePanel={
          <WelcomePanel
            onLoginSuccess={handleLoginSuccess}
            onLogout={handleLogout}
          />
        }
        formPanel={
          isAuthenticated
            ? <InputForm onSubmissionSuccess={handleSuccessSubmit} />
            : <AboutPanel />
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
        counterPanel={
          isAuthenticated
            ? <RagChatPanel />
            : <p style={{ textAlign: 'center', color: '#666' }}>Log in to use the RAG chat.</p>
        }
    />
  );
}

export default App;
