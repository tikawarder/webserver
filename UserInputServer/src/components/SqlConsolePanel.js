import React, { useState } from 'react';
import './css/sqlConsole.css';

function SqlConsolePanel() {
  const [sql, setSql] = useState('SELECT * FROM demo_orders');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleRun = async (e) => {
    e.preventDefault();
    if (!sql.trim()) return;

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await fetch('/api/rawsql/execute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sql }),
      });

      const data = await response.json();

      if (!response.ok) {
        setError(data.error || `Server error: ${response.status}`);
      } else {
        setResult(data);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleLockDemo = async () => {
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await fetch('/api/rawsql/pessimistic-lock/demo?orderId=1&holdMillis=1500', {
        method: 'POST',
      });
      const data = await response.json();
      if (!response.ok) {
        setError(data.error || `Server error: ${response.status}`);
      } else {
        setResult(data);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="sql-console-container">
      <div className="sql-console-top">
        <h2>SQL Console</h2>
        <form onSubmit={handleRun} className="sql-console-form">
          <textarea
            value={sql}
            onChange={(e) => setSql(e.target.value)}
            placeholder="SELECT * FROM demo_orders"
            rows={4}
            data-testid="sql-console-input"
          />
          <div className="sql-console-buttons">
            <button type="submit" disabled={loading} data-testid="sql-console-run">
              {loading ? 'Running...' : 'Run'}
            </button>
            <button type="button" onClick={handleLockDemo} disabled={loading} data-testid="sql-console-lock-demo">
              Pessimistic Lock Demo
            </button>
          </div>
        </form>
      </div>

      <div className="sql-console-bottom">
        {error && <pre className="sql-console-error" data-testid="sql-console-error">{error}</pre>}
        {result && <pre className="sql-console-result" data-testid="sql-console-result">{JSON.stringify(result, null, 2)}</pre>}
      </div>
    </div>
  );
}

export default SqlConsolePanel;
