import React, { useState } from 'react';

function InputForm({ onSubmissionSuccess }) {
  const [formData, setFormData] = useState({
    name: '',
    birthDay: '',
    city: '',
  });
  const [submissionStatus, setSubmissionStatus] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setSubmissionStatus('Sending is ongoing...');

    try {
      // sending with POST
      const response = await fetch('/api/persons', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setSubmissionStatus('Successfully sent, refreshing the list...');
        setFormData({ name: '', birthDay: '', city: '' });

        // Calling this callback, that refreshes the list in App.js
        onSubmissionSuccess();

      } else {
        const errorText = await response.text();
        throw new Error(`Error during sending: ${response.status} - ${errorText}`);
      }

    } catch (error) {
      console.error('Post error:', error);
      setSubmissionStatus(`Error when connecting to server: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px', border: '1px solid #eee', borderRadius: '8px' }}>

      <form onSubmit={handleSubmit}>

        {/* name*/}
        <div style={{ marginBottom: '10px' }}>
          <label htmlFor="name">Name:</label>
          <input type="text" id="name" name="name"
            value={formData.name}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        {/* birthday */}
        <div style={{ marginBottom: '10px' }}>
          <label htmlFor="birthDay">Birth of date:</label>
          <input type="date" id="birthDay" name="birthDay"
            value={formData.birthDay}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        {/* city */}
        <div style={{ marginBottom: '10px' }}>
          <label htmlFor="city">City of birth:</label>
          <input type="text" id="city" name="city"
            value={formData.city}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        <button type="submit" disabled={loading} style={{ padding: '10px 15px', backgroundColor: loading ? '#ccc' : '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
          {loading ? 'Küldés...' : 'Adat Küldése'}
        </button>
      </form>

      {submissionStatus && <p style={{ marginTop: '15px', color: submissionStatus.includes('Hiba') ? 'red' : 'green' }}>{submissionStatus}</p>}
    </div>
  );
}

export default InputForm;