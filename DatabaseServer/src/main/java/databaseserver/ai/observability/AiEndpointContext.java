package databaseserver.ai.observability;

/*
 * Carries the current request's endpoint path across to the ChatModelListener,
 * which has no direct visibility into which controller triggered the LLM call.
 * Set by AiEndpointInterceptor for the lifetime of one HTTP request/thread.
 */
public final class AiEndpointContext {

    private static final ThreadLocal<String> CURRENT_ENDPOINT = new ThreadLocal<>();

    private AiEndpointContext() {
    }

    public static void set(String endpoint) {
        CURRENT_ENDPOINT.set(endpoint);
    }

    public static String get() {
        String endpoint = CURRENT_ENDPOINT.get();
        return endpoint != null ? endpoint : "unknown";
    }

    public static void clear() {
        CURRENT_ENDPOINT.remove();
    }
}
