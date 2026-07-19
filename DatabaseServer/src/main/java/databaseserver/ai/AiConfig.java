package databaseserver.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /*
     * Exposes the LLM as a Spring bean so it can be injected anywhere.
     * Returning the interface (ChatLanguageModel) instead of the concrete class
     * means the rest of the app never depends on Gemini specifically —
     * swapping to Claude or GPT-4 requires changing only this file.
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.0)
                .timeout(Duration.ofSeconds(30))
                .maxRetries(1)
                .build();
    }

    /*
     * Separate model from chatLanguageModel() — this one only turns text into
     * a vector (768 floats, truncated from gemini-embedding-001's native 3072
     * via outputDimensionality), it never generates text.
     * Used by the RAG ingestion and retrieval steps (see ai/rag package).
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return GoogleAiEmbeddingModel.builder()
                .apiKey(geminiApiKey)
                .modelName("gemini-embedding-001")
                .outputDimensionality(768)
                .timeout(Duration.ofSeconds(30))
                .maxRetries(1)
                .build();
    }
}
