package databaseserver.ai.observability;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;

/*
 * Registered once on the shared ChatLanguageModel bean (see AiConfig), this fires
 * for every chatModel.generate(...) call in the app — including the ones made
 * invisibly inside the Phase 4 agent's ReAct loop (JobApplicationAgent's proxy).
 * One listener therefore covers all chat call sites without touching each service.
 */
@Component
public class AiCallLoggingListener implements ChatModelListener {

    private static final String START_NANOS_KEY = "aiCallStartNanos";
    private static final String SPAN_KEY = "aiCallSpan";

    private final AiCallLogService aiCallLogService;
    private final Tracer tracer;

    public AiCallLoggingListener(AiCallLogService aiCallLogService, Tracer tracer) {
        this.aiCallLogService = aiCallLogService;
        this.tracer = tracer;
    }

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        requestContext.attributes().put(START_NANOS_KEY, System.nanoTime());
        Span span = tracer.nextSpan()
                .name("ai-chat-call")
                .tag("endpoint", AiEndpointContext.get())
                .tag("model", requestContext.request().model())
                .start();
        requestContext.attributes().put(SPAN_KEY, span);
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        endSpan(responseContext.attributes());
        long latencyMs = latencyMs(responseContext.attributes());
        TokenUsage tokenUsage = responseContext.response().tokenUsage();

        aiCallLogService.record(
                AiEndpointContext.get(),
                responseContext.response().model(),
                responseContext.request().messages().toString(),
                tokenUsage != null ? tokenUsage.inputTokenCount() : null,
                tokenUsage != null ? tokenUsage.outputTokenCount() : null,
                latencyMs
        );
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        endSpan(errorContext.attributes());
    }

    private long latencyMs(java.util.Map<Object, Object> attributes) {
        Long startNanos = (Long) attributes.get(START_NANOS_KEY);
        return startNanos != null ? (System.nanoTime() - startNanos) / 1_000_000 : 0;
    }

    private void endSpan(java.util.Map<Object, Object> attributes) {
        Span span = (Span) attributes.get(SPAN_KEY);
        if (span != null) {
            span.end();
        }
    }
}
