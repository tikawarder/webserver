import React, { useState } from 'react';
import './css/inputForm.css';

function InputForm({ onSubmissionSuccess }) {
  const [loading, setLoading] = useState(false);
  const [globalError, setGlobalError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault();
    const form = e.currentTarget;

    setLoading(true);
    setGlobalError(null);
    setFieldErrors({});

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

      if (response.status === 401 || response.status === 403) {
         throw new Error('Access denied. Please login!');
      }

      if (response.status === 400) {
                const errorData = await response.json();
                setFieldErrors(errorData);
                return;
            }

      if (!response.ok) {
              throw new Error(`Server error: ${response.status}`);
            }

      form.reset();
      onSubmissionSuccess();

    } catch (err) {
      setGlobalError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
             <div className="form-container">
               <form onSubmit={handleSubmit}>

                 {globalError && (
                              <div style={{ color: 'red', marginBottom: '15px', padding: '10px', backgroundColor: '#ffe6e6', borderRadius: '5px' }}>
                                  ⚠️ {globalError}
                              </div>
                          )}

                 <div className="field-group">
                   <label>Name:</label>
                   <input type="text" name="name"/>
                   {fieldErrors.name && <span style={{color: 'red', fontSize: '0.8rem'}}>{fieldErrors.name}</span>}
                 </div>

                 <div className="field-group">
                   <label>Date of Birth:</label>
                   <input type="date" name="birthDay"/>
                   {fieldErrors.birthDay && <span style={{color: 'red', fontSize: '0.8rem'}}>{fieldErrors.birthDay}</span>}
                 </div>

                 <div className="field-group">
                   <label>City:</label>
                   <input type="text" name="city" />
                   {fieldErrors.city && <span style={{color: 'red', fontSize: '0.8rem'}}>{fieldErrors.city}</span>}
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