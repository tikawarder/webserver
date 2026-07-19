package databaseserver.ai;

import databaseserver.model.dto.PersonDto;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AiSummaryServiceTest {

    @MockBean
    ChatLanguageModel chatModel;

    @Autowired
    AiSummaryService aiSummaryService;

    @Test
    void summarize_shouldReturnLlmGeneratedText() {
        when(chatModel.generate(anyString())).thenReturn(
                "Experienced backend engineer based in Budapest, specializing in Java and Spring Boot."
        );

        PersonDto person = new PersonDto();
        person.setName("Tamas Biro");
        person.setCity("Budapest");

        String summary = aiSummaryService.summarize(person);

        assertThat(summary).isEqualTo(
                "Experienced backend engineer based in Budapest, specializing in Java and Spring Boot."
        );
    }

    @Test
    void summarize_shouldFillPromptTemplateWithPersonData() {
        when(chatModel.generate(anyString())).thenReturn("summary");

        PersonDto person = new PersonDto();
        person.setName("Tamas Biro");
        person.setCity("Budapest");

        aiSummaryService.summarize(person);

        verify(chatModel).generate(contains("Tamas Biro"));
        verify(chatModel).generate(contains("Budapest"));
    }
}
