-- Records every LLM call (chat completions and embeddings) made through
-- databaseserver.ai for cost/latency observability. The prompt itself is
-- never stored, only its SHA-256 hash, to avoid persisting PII.
CREATE TABLE ai_call_log (
    id BIGSERIAL PRIMARY KEY,
    endpoint VARCHAR(255) NOT NULL,
    model VARCHAR(100) NOT NULL,
    prompt_hash VARCHAR(64) NOT NULL,
    input_tokens INTEGER,
    output_tokens INTEGER,
    latency_ms BIGINT NOT NULL,
    cost_usd NUMERIC(10,6),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
