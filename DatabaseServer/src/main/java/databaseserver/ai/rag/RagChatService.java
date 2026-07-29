package databaseserver.ai.rag;

import databaseserver.ai.observability.AiCallLogService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class RagChatService {

    private static final int TOP_K = 3;

    private final EmbeddingModel embeddingModel;
    private final ChatLanguageModel chatModel;
    private final JdbcTemplate jdbcTemplate;
    private final AiCallLogService aiCallLogService;

    public RagChatService(EmbeddingModel embeddingModel, ChatLanguageModel chatModel, JdbcTemplate jdbcTemplate,
                           AiCallLogService aiCallLogService) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
        this.jdbcTemplate = jdbcTemplate;
        this.aiCallLogService = aiCallLogService;
    }

    public String answer(String question) {
        List<String> relevantChunks = retrieveRelevantChunks(question);

        String template = loadPromptTemplate("prompts/rag-chat-v1.txt");
        String prompt = PromptTemplate.from(template)
                .apply(Map.of(
                        "context", String.join("\n---\n", relevantChunks),
                        "question", question
                ))
                .text();

        return chatModel.generate(prompt);
    }

    // R — Retrieval: embed the question, find the closest stored chunks by cosine distance.
    private List<String> retrieveRelevantChunks(String question) {
        Embedding questionEmbedding = aiCallLogService.recordEmbeddingCall(
                "/api/ai/rag-chat", "gemini-embedding-001", question,
                () -> embeddingModel.embed(question).content()
        );
        return jdbcTemplate.queryForList(
                "SELECT content FROM rag_chunks ORDER BY embedding <=> ?::vector LIMIT ?",
                String.class,
                PgVectorFormat.toLiteral(questionEmbedding),
                TOP_K
        );
    }

    private String loadPromptTemplate(String path) {
        try {
            return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + path, e);
        }
    }
}
