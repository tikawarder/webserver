import React, { useState } from 'react';

function WelcomePanel() {
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
                <h1>Welcome back, {username}!</h1>
                <p>You are successfully logged in.</p>
                <button onClick={() => {
                    setIsLoggedIn(false);
                    setStatus('');
                    setUsername('');
                    setPassword('');
                    setFieldErrors({});
                }}>Logout</button>
            </div>
        );
    }

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '8px', maxWidth: '400px', margin: '20px auto' }}>
            <h1>Login</h1>
            <p>Please enter your credentials:</p>

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="username">Username: </label>
                    <input
                        type="text"
                        id="username"
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
                <button type="submit" style={{ padding: '10px 20px', cursor: 'pointer' }}>Login</button>
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