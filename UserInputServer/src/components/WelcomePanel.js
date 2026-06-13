import React, { useState } from 'react';

function WelcomePanel({ onLoginSuccess, onLogout }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [status, setStatus] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault(); // Prevent page reload
        setStatus('Sending...');
        setFieldErrors({});

        // Ensure keys match the Java DTO field names exactly
        const loginData = {
            username: username,
            password: password
        };

        try {
            const response = await fetch('/api/auth', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
                credentials: 'include'
            });

            if (response.ok) {
                setStatus('Login successful! Cookie set.');
                setIsLoggedIn(true);
                setFieldErrors({});
                if (onLoginSuccess) {
                    onLoginSuccess();
                }

            } else if (response.status === 400) {
                            const errorData = await response.json();
                            setFieldErrors(errorData);
                            setStatus('Wrong input data, please modify.');
            } else {
                setStatus('Login failed: Invalid credentials.');
            }
        } catch (error) {
            setStatus('A network error occurred. Is the backend running?');
            console.error('Error:', error);
        }
    };

    if (isLoggedIn) {
        return (
            <div style={{ padding: '20px', textAlign: 'center' }}>
                <h1 data-testid="welcome-message">Welcome back, {username}!</h1>
                <p>You are successfully logged in.</p>
                <button data-testid="logout-button" onClick={async () => {
                    await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                    setIsLoggedIn(false);
                    setStatus('');
                    setUsername('');
                    setPassword('');
                    setFieldErrors({});
                    if (onLogout) onLogout();
                }}>Logout</button>
            </div>
        );
    }

    return (
        <div style={{ width: '100%', maxWidth: '400px' }}>
            <h1>Login</h1>
            <p>Please enter your credentials:</p>

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="username">Username: </label>
                    <input
                        type="text"
                        id="username"
                        data-testid="username-input"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
//                        required
                        style={{ width: '100%', padding: '8px', marginTop: '5px', border: fieldErrors.username ? '2px solid red' : '1px solid #ccc' }}

                    />
                    {fieldErrors.username && (
                                            <span style={{ color: 'red', fontSize: '0.9em' }}>
                                                {fieldErrors.username}
                                            </span>
                                        )}
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="password">Password: </label>
                    <input
                        type="password"
                        id="password"
                        data-testid="password-input"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
//                        required
                        style={{ width: '100%', padding: '8px', marginTop: '5px', border: fieldErrors.password ? '2px solid red' : '1px solid #ccc' }}
                    />
                    {fieldErrors.password && (
                                            <span style={{ color: 'red', fontSize: '0.9em' }}>
                                                {fieldErrors.password}
                                            </span>
                                        )}
                </div>
                <button type="submit" data-testid="login-button" style={{ padding: '10px 20px', cursor: 'pointer' }}>Login</button>
            </form>

            {status && (
                <p style={{
                    marginTop: '15px',
                    fontWeight: 'bold',
                    color: status.includes('failed') || status.includes('error') ? 'red' : 'green'
                }}>
                    {status}
                </p>
            )}
        </div>
    );
}

export default WelcomePanel;