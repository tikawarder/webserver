package databaseserver.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class SkillExtractorService {

    private final ChatLanguageModel chatModel;
    private final ObjectMapper objectMapper;

    public SkillExtractorService(ChatLanguageModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    public SkillsDto extract(String jobDescription) {
        String template = loadPromptTemplate("prompts/extract-skills-v1.txt");
        String prompt = PromptTemplate.from(template)
                .apply(Map.of("job_description", jobDescription))
                .text();

        String rawResponse = chatModel.generate(prompt);
        return parseSkillsDto(rawResponse);
    }

    private String loadPromptTemplate(String path) {
        try {
            return new ClassPathResource(path)
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + path, e);
        }
    }

    private SkillsDto parseSkillsDto(String json) {
        try {
            // Strip markdown code fences if the model wraps the JSON anyway
            String cleaned = json.strip();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").strip();
            }
            return objectMapper.readValue(cleaned, SkillsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("LLM returned unparseable JSON: " + json, e);
        }
    }
}
