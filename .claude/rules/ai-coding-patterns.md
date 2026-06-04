# AI Coding Patterns

## Package structure
All AI code lives under `databaseserver/ai/`:
- `ai/` — controllers and top-level services
- `ai/rag/` — embedding, chunking, vector search
- `ai/agent/` — agents and tool definitions
- `ai/observability/` — call logging, metrics

## Prompt files
- Stored in `DatabaseServer/src/main/resources/prompts/`
- Named `[task]-v[N].txt` — never overwrite, always increment version
- Variables use `{{double_braces}}` placeholder syntax

## LLM client
- Use **LangChain4j** — not raw HTTP, not OpenAI SDK directly
- Inject the AI service as a Spring `@Service` bean
- Never instantiate the LLM client inline in a controller

## Testing
- Mock LLM responses with **WireMock** — never call the real API in unit tests
- Integration tests that do call the API go in a separate `*IT.java` class and are skipped in CI unless `AI_INTEGRATION_TEST=true`

## Error handling
- Always handle `RateLimitException` and `TimeoutException` from the LLM client
- Log every failed LLM call with the prompt hash (not the full prompt — it may contain PII)
