package databaseserver.ai.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.ai.SkillExtractorService;
import databaseserver.ai.SkillsDto;
import databaseserver.ai.rag.RagChatService;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import org.jsoup.Jsoup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
 * The tools an LLM agent is allowed to call (see JobApplicationAgent). Each @Tool
 * method is a plain Java method with no agent-specific logic of its own — three of
 * the four simply delegate to the existing Phase 1-3 services, unchanged. Only
 * generateCoverLetterSection is new AI logic.
 */
@Component
public class AgentTools {

    private final SkillExtractorService skillExtractorService;
    private final RagChatService ragChatService;
    private final ChatLanguageModel chatModel;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public AgentTools(SkillExtractorService skillExtractorService,
                       RagChatService ragChatService,
                       ChatLanguageModel chatModel,
                       ObjectMapper objectMapper) {
        this.skillExtractorService = skillExtractorService;
        this.ragChatService = ragChatService;
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    @Tool("Fetches the raw text content of a job posting from a URL")
    public String fetchJobDescription(String url) {
        String html = restTemplate.getForObject(url, String.class);
        if (html == null) {
            return "";
        }
        return Jsoup.parse(html).text();
    }

    @Tool("Extracts required and nice-to-have skills from a job description as JSON")
    public String extractSkills(String jobDescriptionText) {
        SkillsDto skills = skillExtractorService.extract(jobDescriptionText);
        try {
            return objectMapper.writeValueAsString(skills);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize SkillsDto", e);
        }
    }

    @Tool("Answers a question about the candidate's own professional experience")
    public String queryCandidateExperience(String question) {
        return ragChatService.answer(question);
    }

    @Tool("Generates a short cover letter paragraph matching required skills to the candidate's experience")
    public String generateCoverLetterSection(String requiredSkills, String matchingExperience) {
        String template = loadPromptTemplate("prompts/cover-letter-section-v1.txt");
        String prompt = PromptTemplate.from(template)
                .apply(Map.of(
                        "required_skills", requiredSkills,
                        "matching_experience", matchingExperience
                ))
                .text();
        return chatModel.generate(prompt);
    }

    private String loadPromptTemplate(String path) {
        try {
            return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + path, e);
        }
    }
}
