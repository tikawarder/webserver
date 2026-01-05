import React, { useState } from 'react';
import './css/inputForm.css';

function InputForm({ onSubmissionSuccess }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const form = e.currentTarget;

    setLoading(true);
    setError(null);

    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    try {
      const response = await fetch('/api/persons', {
        method: 'POST',
        headers: {
         'Content-Type': 'application/json'},
        body: JSON.stringify(data),
        credentials: 'include'
      });

      if (response.status === 403 || response.status === 401) {
         throw new Error('Access denied. Please login!');
      }

      if (!response.ok) {
              throw new Error(`Server error: ${response.status}`);
            }

      form.reset();
      onSubmissionSuccess();

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
             <div className="form-container">
               <form onSubmit={handleSubmit}>

                 {error && (
                             <div style={{ color: 'red', marginBottom: '10px', fontWeight: 'bold' }}>
                                 {error}
                             </div>
                          )}

                 <div className="field-group">
                   <label>Name:</label>
                   <input type="text" name="name" required />
                 </div>

                 <div className="field-group">
                   <label>Date of Birth:</label>
                   <input type="date" name="birthDay" required />
                 </div>

                 <div className="field-group">
                   <label>City:</label>
                   <input type="text" name="city" required />
                 </div>

                 <button
                   type="submit"
                   className="submit-button"
                   disabled={loading}
                 >
                   {loading ? 'Sending...' : 'Saving data'}
                 </button>

               </form>
             </div>
           );
}

export default InputForm;