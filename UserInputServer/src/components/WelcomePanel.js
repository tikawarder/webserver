import React, { useState } from 'react';

function WelcomePanel() {
    // State for input fields and feedback
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [status, setStatus] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault(); // Prevent page reload
        setStatus('Sending...');

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
            });

            if (response.ok) {
                // Since the controller returns a boolean, 'result' will be true or false
                const isAuthorized = await response.json();

                if (isAuthorized === true) {
                    setStatus('Login successful!');
                } else {
                    setStatus('Login failed: Invalid username or password.');
                }
            } else {
                setStatus('Login failed. Server returned status: ' + response.status);
            }
        } catch (error) {
            setStatus('A network error occurred. Please check if the backend is running.');
        }
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '8px', maxWidth: '400px', margin: '20px auto' }}>
            <h1>Welcome!</h1>
            <p>Please log in to continue:</p>

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="username">Username: </label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                    />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label htmlFor="password">Password: </label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                    />
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