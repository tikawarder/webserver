import React, { useState } from 'react';
import keycloak from '../keycloak';
import './css/ragChatPanel.css';

function RagChatPanel() {
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [ingesting, setIngesting] = useState(false);
  const [ingestMessage, setIngestMessage] = useState(null);
  const [error, setError] = useState(null);

  const handleIngest = async () => {
    setIngesting(true);
    setError(null);
    setIngestMessage(null);

    try {
      await keycloak.updateToken(30);

      const response = await fetch('/api/ai/rag-chat/ingest', {
        method: 'POST',
        headers: { 'Authorization': 'Bearer ' + keycloak.token },
      });

      if (response.status === 401 || response.status === 403) {
        throw new Error('Access denied. Please login!');
      }
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }

      const data = await response.json();
      setIngestMessage(`Stored ${data.chunksStored} chunks.`);
    } catch (err) {
      setError(err.message);
    } finally {
      setIngesting(false);
    }
  };

  const handleAsk = async (e) => {
    e.preventDefault();
    if (!question.trim()) return;

    setLoading(true);
    setError(null);
    setAnswer(null);

    try {
      await keycloak.updateToken(30);

      const response = await fetch('/api/ai/rag-chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + keycloak.token,
        },
        body: JSON.stringify({ question }),
      });

      if (response.status === 401 || response.status === 403) {
        throw new Error('Access denied. Please login!');
      }
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }

      const data = await response.json();
      setAnswer(data.answer);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="rag-chat-container">
      <h2>RAG Chat — Kubernetes knowledge base</h2>

      <button
        type="button"
        onClick={handleIngest}
        disabled={ingesting}
        className="ingest-button"
        data-testid="rag-ingest-button"
      >
        {ingesting ? 'Ingesting...' : 'Re-ingest source document'}
      </button>
      {ingestMessage && <p className="ingest-message" data-testid="rag-ingest-message">{ingestMessage}</p>}

      <form onSubmit={handleAsk} className="rag-chat-form">
        <input
          type="text"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="Ask a question about Kubernetes..."
          data-testid="rag-question-input"
        />
        <button type="submit" disabled={loading} data-testid="rag-ask-button">
          {loading ? 'Thinking...' : 'Ask'}
        </button>
      </form>

      {error && <div className="error-message" data-testid="rag-error">⚠️ {error}</div>}
      {answer && <div className="rag-answer" data-testid="rag-answer">{answer}</div>}
    </div>
  );
}

export default RagChatPanel;
