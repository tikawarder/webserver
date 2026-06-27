package databaseserver.ai;

import databaseserver.model.dto.PersonDto;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class AiSummaryService {

    private final ChatLanguageModel chatModel;

    public AiSummaryService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public String summarize(PersonDto person) {
        String template = loadPromptTemplate("prompts/summarize-person-v1.txt");
        String prompt = PromptTemplate.from(template)
                .apply(Map.of("name", person.getName(), "city", person.getCity()))
                .text();
        return chatModel.generate(prompt);
    }

    private String loadPromptTemplate(String path) {
        try {
            return new ClassPathResource(path)
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + path, e);
        }
    }
}
