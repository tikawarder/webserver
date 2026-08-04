package databaseserver.ai.rag;

import databaseserver.ai.observability.AiCallLogService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class RagIngestionService {

    private static final int CHUNK_SIZE_WORDS = 80;
    private static final int OVERLAP_WORDS = 15;

    private final EmbeddingModel embeddingModel;
    private final JdbcTemplate jdbcTemplate;
    private final AiCallLogService aiCallLogService;
    private final Tracer tracer;
    private final ExecutorService ragIngestionExecutor;

    public RagIngestionService(EmbeddingModel embeddingModel, JdbcTemplate jdbcTemplate,
                                AiCallLogService aiCallLogService, Tracer tracer,
                                ExecutorService ragIngestionExecutor) {
        this.embeddingModel = embeddingModel;
        this.jdbcTemplate = jdbcTemplate;
        this.aiCallLogService = aiCallLogService;
        this.tracer = tracer;
        this.ragIngestionExecutor = ragIngestionExecutor;
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

        // Captured here because Tracer's "current span" is thread-local — without
        // rebinding it inside each worker task, embedding spans would become
        // orphaned roots instead of children of this request's trace.
        Span callerSpan = tracer.currentSpan();

        List<CompletableFuture<Void>> submittedTasks = feedThreadPool(chunks, callerSpan);
        waitForAllToFinish(submittedTasks);

        return chunks.size();
    }

    private List<CompletableFuture<Void>> feedThreadPool(List<String> chunks, Span callerSpan) {
        List<CompletableFuture<Void>> submittedTasks = new ArrayList<>();
        for (String chunk : chunks) {
            CompletableFuture<Void> task = CompletableFuture.runAsync(
                    () -> embedAndStore(chunk, callerSpan), ragIngestionExecutor);
            submittedTasks.add(task);
        }
        return submittedTasks;
    }

    private void waitForAllToFinish(List<CompletableFuture<Void>> submittedTasks) {
        CompletableFuture.allOf(submittedTasks.toArray(new CompletableFuture[0])).join();
    }

    private void embedAndStore(String chunk, Span callerSpan) {
        if (callerSpan == null) {
            doEmbedAndStore(chunk);
            return;
        }
        try (Tracer.SpanInScope ignored = tracer.withSpan(callerSpan)) {
            doEmbedAndStore(chunk);
        }
    }

    // JdbcTemplate is thread-safe (each call borrows its own pooled Connection),
    // so — unlike the Outbox/JPA case — the insert can run on the same worker
    // thread as the embedding call, no separate transaction needed.
    private void doEmbedAndStore(String chunk) {
        Embedding embedding = aiCallLogService.recordEmbeddingCall(
                "/api/ai/rag-chat/ingest", "gemini-embedding-001", chunk,
                () -> embeddingModel.embed(chunk).content()
        );
        jdbcTemplate.update(
                "INSERT INTO rag_chunks (content, embedding) VALUES (?, ?::vector)",
                chunk,
                PgVectorFormat.toLiteral(embedding)
        );
        log.info("Embedded and stored chunk on thread {}", Thread.currentThread().getName());
    }

    private String loadSourceText() {
        try {
            return new ClassPathResource("rag/kubernetes-overview.txt").getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load rag/kubernetes-overview.txt", e);
        }
    }
}
