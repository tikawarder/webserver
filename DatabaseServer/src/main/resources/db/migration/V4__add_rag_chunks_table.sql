-- Enables the pgvector extension and creates the storage for the RAG pipeline.
-- Each row holds one text chunk from the RAG source document plus its embedding
-- (768 dims: gemini-embedding-001 truncated via outputDimensionality, Matryoshka representation).
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE rag_chunks (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    embedding VECTOR(768) NOT NULL
);
