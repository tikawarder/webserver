// src/components/InputForm.js
import React, { useState } from 'react';

function InputForm() {
  // Állapot (state) a form adatok tárolására
  const [formData, setFormData] = useState({
    name: '',
    birthDay: '', // Fontos, hogy a formátum illeszkedjen a Java LocalDate.parse-hoz!
    city: '',
  });
  const [submissionStatus, setSubmissionStatus] = useState('');
  const [loading, setLoading] = useState(false);

  // Az összes input mező értékének frissítése egyetlen eseménykezelővel
  const handleChange = (e) => {
    setFormData({
      ...formData, // Megtartja a többi mező értékét
      [e.target.name]: e.target.value, // Frissíti a változó nevét a "name" attribútum alapján
    });
  };

  // Aszinkron eseménykezelő a küldésre
const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setSubmissionStatus('Küldés folyamatban...');

    try {
      // 1. POST kérés küldése a DatabaseServerre
      // Útvonal: /api/persons (ez a proxy-n keresztül a http://localhost:8081/api/persons-ra megy)
      const response = await fetch('/api/persons', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        // 2. A JS objektumot JSON stringgé alakítjuk küldés előtt
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        // A 200 OK vagy 204 No Content (ha void a POST) a sikeres válasz
        setSubmissionStatus('Sikeresen mentve a DatabaseServerre!');
        setFormData({ name: '', birthDay: '', city: '' }); // Form ürítése
      } else {
        // Hiba esetén megpróbáljuk kiolvasni a hibaüzenetet a válaszból
        const errorText = await response.text();
        throw new Error(`Küldési hiba: ${response.status} - ${errorText}`);
      }

    } catch (error) {
      console.error('Küldés sikertelen:', error);
      setSubmissionStatus(`Hiba a DatabaseServerrel való kommunikációban: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Felhasználó adatok felvitele</h2>

      <form onSubmit={handleSubmit}>

        {/* Név mező */}
        <div style={{ marginBottom: '10px' }}>
          <label htmlFor="name">Név:</label>
          <input type="text" id="name" name="name"
            value={formData.name} // Vezérelt (Controlled) komponens: érték a state-ből
            onChange={handleChange} // Változáskor a state frissül
            required
            disabled={loading}
          />
        </div>

        {/* Születési dátum mező */}
        <div style={{ marginBottom: '10px' }}>
          <label htmlFor="birthDay">Születési dátum:</label>
          <input type="date" id="birthDay" name="birthDay"
            value={formData.birthDay}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        {/* Város mező */}
        <div style={{ marginBottom: '10px' }}>
          <label htmlFor="city">Születési város:</label>
          <input type="text" id="city" name="city"
            value={formData.city}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Küldés...' : 'Adat Küldése a Java Backendnek'}
        </button>
      </form>

      {/* Visszajelzés a felhasználónak */}
      {submissionStatus && <p style={{ marginTop: '15px', color: submissionStatus.includes('Hiba') ? 'red' : 'green' }}>{submissionStatus}</p>}
    </div>
  );
}

export default InputForm;