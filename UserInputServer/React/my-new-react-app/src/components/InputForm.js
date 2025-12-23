import React, { useState } from 'react';
import './css/inputForm.css';

function InputForm({ onSubmissionSuccess }) {
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const formData = new FormData(e.currentTarget);
    const data = Object.fromEntries(formData.entries());

    try {
      const response = await fetch('/api/persons', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        e.currentTarget.reset();
        onSubmissionSuccess();
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
             <div className="form-container">

               <form onSubmit={handleSubmit}>

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