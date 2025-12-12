// src/App.js

import React, { useState, useEffect } from 'react';

function App() {
  const [backendResponse, setBackendResponse] = useState("Adat kérése a Java szervertől...");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/persons')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP hiba! Státusz: ${response.status}`);
        }
        // **********************************************
        // VÁLTOZÁS: response.json() használata
        return response.json();
        // **********************************************
      })
      .then(data => {
        // 1. Átalakítjuk az objektumot olvasható formába (pl. Stringify)
        // 2. Különösen keressük az adatban lévő kulcsokat a szebb megjelenítéshez

        let displayMessage = JSON.stringify(data, null, 2); // Gyönyörű formázott JSON

        // Ha a JSON tartalmaz "message" vagy "status" mezőt, azt használjuk
        if (data && (data.message || data.status)) {
            displayMessage = `Sikeres kapcsolat. Válasz: ${data.message || data.status}`;
        }

        setBackendResponse(displayMessage);
        setLoading(false);
      })
      .catch(error => {
        console.error("API hiba történt:", error);
        setBackendResponse(`HIBA a kapcsolódáskor: ${error.message}. Ellenőrizd a Tomcat szervert!`);
        setLoading(false);
      });
  }, []);

  return (
    <div style={{ padding: '20px' }}>
      <h1>React és Java API Teszt (JSON Kezelés)</h1>
      <p>Hívott végpont a proxy-n keresztül: <code>/api/persons</code></p>
      <hr />

      <h2>Eredmény:</h2>
      {loading ? (
        <p>Betöltés...</p>
      ) : (
        <pre style={{
          padding: '10px',
          border: '1px solid',
          backgroundColor: '#f7f7f7',
          color: backendResponse.includes('HIBA') ? 'red' : 'green',
          whiteSpace: 'pre-wrap' // Hosszabb szövegek tördeléséhez
        }}>
          {backendResponse}
        </pre>
      )}
    </div>
  );
}

export default App;