import React from 'react';
import keycloak from '../keycloak';

function WelcomePanel({ onLoginSuccess, onLogout }) {

    const handleLogin = () => {
        keycloak.login();
    };

    const handleLogout = () => {
        keycloak.logout({ redirectUri: window.location.origin });
        if (onLogout) onLogout();
    };

    if (keycloak.authenticated) {
        const username = keycloak.tokenParsed?.preferred_username || 'User';
        const roles = keycloak.tokenParsed?.realm_access?.roles?.filter(
            r => !r.startsWith('default-roles-')
        ) || [];

        return (
            <div style={{ padding: '20px', textAlign: 'center' }}>
                <h1 data-testid="welcome-message">Welcome back, {username}!</h1>
                <p>Roles: {roles.join(', ')}</p>
                <button data-testid="logout-button" onClick={handleLogout}>Logout</button>
            </div>
        );
    }

    return (
        <div style={{ width: '100%', maxWidth: '400px', textAlign: 'center' }}>
            <h1>Welcome</h1>
            <p>Please sign in to access the application.</p>
            <button
                data-testid="login-button"
                onClick={handleLogin}
                style={{ padding: '10px 20px', cursor: 'pointer' }}
            >
                Sign in with Keycloak
            </button>
        </div>
    );
}

export default WelcomePanel;