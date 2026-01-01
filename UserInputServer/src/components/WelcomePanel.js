import React, { useState } from 'react';

function WelcomePanel() {
    // State for input fields and feedback
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [status, setStatus] = useState('');
    const [isLoggedIn, setIsLoggedIn] = useState(false);

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
               const data = await response.json();
                               if (data.token) {
                                   // 2. MENTÉS: A tokent eltároljuk a böngészőben (localStorage)
                                   // Így frissítés után is bejelentkezve marad a felhasználó
                                   localStorage.setItem('jwtToken', data.token);

                                   setStatus('Login successful! Redirecting...');
                                   setIsLoggedIn(true);

                                   console.log('Token received and stored:', data.token);

                                   // Itt hívhatnál meg egy függvényt, ami átirányít a főoldalra
                                   // pl. onLoginSuccess();
                               } else {
                                   setStatus('Login failed: No token received.');
                               }
                           } else {
                               setStatus('Login failed: Invalid username or password.');
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
                                   localStorage.removeItem('jwtToken'); // Logout
                                   setIsLoggedIn(false);
                                   setStatus('');
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