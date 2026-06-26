package databaseserver.ai;

import databaseserver.model.dto.PersonDto;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class AiSummaryService {

    private final ChatLanguageModel chatModel;

    public AiSummaryService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public String summarize(PersonDto person) {
        String template = loadPromptTemplate("prompts/summarize-person-v1.txt");
        String prompt = fillTemplate(template, person);
        return chatModel.generate(prompt);
    }

    /*
     * Loads the prompt from the classpath so it can be edited without recompiling.
     * This is the standard pattern for externalizing prompts in production systems.
     */
    private String loadPromptTemplate(String path) {
        try {
            return new ClassPathResource(path)
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + path, e);
        }
    }

    /*
     * Simple string substitution for {{placeholders}}.
     * In Phase 2 we will replace this with LangChain4j's PromptTemplate,
     * which is safer and supports more complex variable handling.
     */
    private String fillTemplate(String template, PersonDto person) {
        return template
                .replace("{{name}}", person.getName())
                .replace("{{city}}", person.getCity());
    }
}
