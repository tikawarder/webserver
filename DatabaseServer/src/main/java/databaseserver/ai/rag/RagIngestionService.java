package databaseserver.ai.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RagIngestionService {

    private static final int CHUNK_SIZE_WORDS = 80;
    private static final int OVERLAP_WORDS = 15;

    private final EmbeddingModel embeddingModel;
    private final JdbcTemplate jdbcTemplate;

    public RagIngestionService(EmbeddingModel embeddingModel, JdbcTemplate jdbcTemplate) {
        this.embeddingModel = embeddingModel;
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * Re-reads kubernetes-overview.txt, re-embeds every chunk and replaces
     * whatever was stored before. Safe to call repeatedly while iterating on
     * the source document — never leaves duplicate/stale chunks behind.
     */
    public int ingest() {
        String text = loadSourceText();
        List<String> chunks = TextChunker.chunk(text, CHUNK_SIZE_WORDS, OVERLAP_WORDS);

        jdbcTemplate.update("DELETE FROM rag_chunks");
        for (String chunk : chunks) {
            Embedding embedding = embeddingModel.embed(chunk).content();
            jdbcTemplate.update(
                    "INSERT INTO rag_chunks (content, embedding) VALUES (?, ?::vector)",
                    chunk,
                    PgVectorFormat.toLiteral(embedding)
            );
        }
        return chunks.size();
    }

    private String loadSourceText() {
        try {
            return new ClassPathResource("rag/kubernetes-overview.txt").getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load rag/kubernetes-overview.txt", e);
        }
    }
}
