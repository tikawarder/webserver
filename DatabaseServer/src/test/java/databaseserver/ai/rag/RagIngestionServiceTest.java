package databaseserver.ai.rag;

import databaseserver.ai.observability.AiCallLogService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RagIngestionServiceTest {

    @Mock
    private EmbeddingModel embeddingModel;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private AiCallLogService aiCallLogService;

    @Mock
    private Tracer tracer;

    private ExecutorService executorService;
    private RagIngestionService ragIngestionService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(3);
        ragIngestionService = new RagIngestionService(
                embeddingModel, jdbcTemplate, aiCallLogService, tracer, executorService);

        lenient().when(tracer.currentSpan()).thenReturn(null);
        lenient().when(embeddingModel.embed(anyString()))
                .thenReturn(Response.from(Embedding.from(new float[]{0.1f, 0.2f})));
        lenient().when(aiCallLogService.recordEmbeddingCall(anyString(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Supplier<Embedding> supplier = invocation.getArgument(3);
                    return supplier.get();
                });
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    @DisplayName("Deletes old chunks once, then embeds and inserts every chunk, spread across threads")
    void ingest_clearsOldChunks_thenEmbedsAndInsertsEveryChunkConcurrently() {
        Set<String> threadNames = new CopyOnWriteArraySet<>();
        AtomicInteger insertCount = new AtomicInteger();
        lenient().when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> {
                    threadNames.add(Thread.currentThread().getName());
                    insertCount.incrementAndGet();
                    return 1;
                });

        int chunkCount = ragIngestionService.ingest();

        verify(jdbcTemplate, times(1)).update(eq("DELETE FROM rag_chunks"));
        assertEquals(chunkCount, insertCount.get(), "expected one insert per chunk");
        assertTrue(chunkCount > 3, "expected the source document to produce more than 3 chunks, got: " + chunkCount);
        assertTrue(threadNames.size() > 1, "expected inserts spread across more than one thread, got: " + threadNames);
    }
}
