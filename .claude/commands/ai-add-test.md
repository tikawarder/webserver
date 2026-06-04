# Skill: Add Tests for an AI Service Class

## Usage
`/ai-add-test [ClassName]`

Example: `/ai-add-test AiSummaryService`

## Steps

1. **Read the target class**
   - Find `[ClassName].java` under `databaseserver/ai/`
   - Identify all public methods and their signatures
   - Note what LLM client / LangChain4j interface is injected

2. **Check for existing tests**
   - Look for `[ClassName]Test.java` under `src/test/`
   - If it exists, extend it rather than replacing it

3. **Write unit tests with WireMock**
   - Mock the LLM API endpoint (Anthropic or OpenAI) using WireMock
   - One test per method, plus edge cases:
     - Happy path — valid LLM response
     - Malformed JSON response from LLM
     - Rate limit error (HTTP 429)
     - Timeout
   - Use `@SpringBootTest` with `WireMockServer` or `WireMockExtension`
   - Never call the real API in unit tests

4. **Write one integration test (optional, skipped in CI)**
   - Class named `[ClassName]IT.java`
   - Annotated with a custom `@EnabledIfEnvironmentVariable(named = "AI_INTEGRATION_TEST", matches = "true")`
   - Calls the real API with a minimal, cheap prompt

5. **Learning note**
   - Explain why mocking LLM responses is important (determinism, cost, speed)
   - Explain what WireMock is doing under the hood
   - Name this pattern: "contract testing for external APIs"
