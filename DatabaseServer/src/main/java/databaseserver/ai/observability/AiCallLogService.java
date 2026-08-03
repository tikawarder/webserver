package databaseserver.ai.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.function.Supplier;

/*
 * Single place where every LLM call (chat or embedding) is turned into:
 *  - a persisted AiCallLog row (cost/latency audit trail, prompt hash only — never the raw prompt)
 *  - a Micrometer Counter + Timer, tagged by endpoint/model (scraped by the existing Prometheus setup)
 *  - a Zipkin child span, nested under the current HTTP request trace
 *
 * Illustrative per-1M-token USD pricing — approximate, for demo/interview purposes only.
 */
@Service
public class AiCallLogService {

    private static final Map<String, double[]> PRICE_PER_MILLION_TOKENS = Map.of(
            "gemini-2.5-flash", new double[]{0.30, 2.50},
            "gemini-embedding-001", new double[]{0.15, 0.0}
    );

    private final AiCallLogRepository repository;
    private final MeterRegistry meterRegistry;
    private final Tracer tracer;

    public AiCallLogService(AiCallLogRepository repository, MeterRegistry meterRegistry, Tracer tracer) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
        this.tracer = tracer;
    }

    public void record(String endpoint, String model, String promptText, Integer inputTokens, Integer outputTokens, long latencyMs) {
        AiCallLog log = AiCallLog.builder()
                .endpoint(endpoint)
                .model(model)
                .promptHash(sha256(promptText))
                .inputTokens(inputTokens)
                .outputTokens(outputTokens)
                .latencyMs(latencyMs)
                .costUsd(estimateCost(model, inputTokens, outputTokens))
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(log);

        meterRegistry.counter("ai.calls", "endpoint", endpoint, "model", model).increment();
        meterRegistry.timer("ai.call.duration", "endpoint", endpoint, "model", model)
                .record(Duration.ofMillis(latencyMs));
    }

    /*
     * Embedding calls have no ChatModelListener hook in this langchain4j version,
     * so callers (RagChatService, RagIngestionService) wrap them explicitly here
     * instead of relying on automatic interception like chat calls get.
     */
    public <T> T recordEmbeddingCall(String endpoint, String model, String inputText, Supplier<T> call) {
        long start = System.nanoTime();
        Span span = tracer.nextSpan().name("ai-embedding-call").tag("endpoint", endpoint).tag("model", model).start();
        try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
            T result = call.get();
            long latencyMs = (System.nanoTime() - start) / 1_000_000;
            record(endpoint, model, inputText, estimateTokenCount(inputText), null, latencyMs);
            return result;
        } finally {
            span.end();
        }
    }

    // No token-usage field on the embedding response in this langchain4j version — chars/4 is a rough stand-in.
    private int estimateTokenCount(String text) {
        return Math.max(1, text.length() / 4);
    }

    private BigDecimal estimateCost(String model, Integer inputTokens, Integer outputTokens) {
        double[] pricing = PRICE_PER_MILLION_TOKENS.get(model);
        if (pricing == null) {
            return null;
        }
        double inputCost = (inputTokens != null ? inputTokens : 0) / 1_000_000.0 * pricing[0];
        double outputCost = (outputTokens != null ? outputTokens : 0) / 1_000_000.0 * pricing[1];
        return BigDecimal.valueOf(inputCost + outputCost);
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
