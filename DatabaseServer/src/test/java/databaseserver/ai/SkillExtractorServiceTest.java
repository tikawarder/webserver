package databaseserver.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class SkillExtractorServiceTest {

    @MockBean
    ChatLanguageModel chatModel;

    @Autowired
    SkillExtractorService skillExtractorService;

    @Test
    void extract_shouldParseRequiredSkillsAndSeniority() {
        when(chatModel.generate(anyString())).thenReturn(
                """
                {"requiredSkills":["Java 17","Spring Boot","Kafka"],\
                "niceToHave":["Kubernetes","AWS"],\
                "seniority":"mid-level"}
                """
        );

        SkillsDto result = skillExtractorService.extract("We need a Java developer...");

        assertThat(result.requiredSkills()).containsExactly("Java 17", "Spring Boot", "Kafka");
        assertThat(result.niceToHave()).containsExactly("Kubernetes", "AWS");
        assertThat(result.seniority()).isEqualTo("mid-level");
    }

    @Test
    void extract_shouldHandleMarkdownCodeFences() {
        when(chatModel.generate(anyString())).thenReturn(
                """
                ```json
                {"requiredSkills":["Java"],"niceToHave":[],"seniority":"junior"}
                ```
                """
        );

        SkillsDto result = skillExtractorService.extract("Junior Java developer needed.");

        assertThat(result.requiredSkills()).containsExactly("Java");
        assertThat(result.seniority()).isEqualTo("junior");
    }

    @Test
    void extract_shouldThrowWhenResponseIsNotJson() {
        when(chatModel.generate(anyString())).thenReturn("Sorry, I cannot process this request.");

        assertThatThrownBy(() -> skillExtractorService.extract("some job description"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("LLM returned unparseable JSON");
    }
}
