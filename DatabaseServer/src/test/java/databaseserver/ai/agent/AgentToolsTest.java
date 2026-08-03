package databaseserver.ai.agent;

import com.sun.net.httpserver.HttpServer;
import databaseserver.ai.SkillExtractorService;
import databaseserver.ai.SkillsDto;
import databaseserver.ai.rag.RagChatService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AgentToolsTest {

    @MockBean
    SkillExtractorService skillExtractorService;

    @MockBean
    RagChatService ragChatService;

    @MockBean
    ChatLanguageModel chatModel;

    @Autowired
    AgentTools agentTools;

    private HttpServer httpServer;

    @AfterEach
    void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @Test
    void fetchJobDescription_shouldStripHtmlAndReturnPlainText() throws Exception {
        String html = "<html><body><h1>Java Developer</h1><p>Spring Boot required.</p></body></html>";
        httpServer = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        httpServer.createContext("/job", exchange -> {
            byte[] body = html.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        httpServer.start();
        String url = "http://localhost:" + httpServer.getAddress().getPort() + "/job";

        String result = agentTools.fetchJobDescription(url);

        assertThat(result).contains("Java Developer").contains("Spring Boot required.");
    }

    @Test
    void extractSkills_shouldDelegateToSkillExtractorServiceAndReturnJson() {
        when(skillExtractorService.extract("some job description"))
                .thenReturn(new SkillsDto(List.of("Java", "Spring Boot"), List.of("Kubernetes"), "mid-level"));

        String result = agentTools.extractSkills("some job description");

        assertThat(result).contains("Java").contains("Spring Boot").contains("Kubernetes").contains("mid-level");
    }

    @Test
    void queryCandidateExperience_shouldDelegateToRagChatService() {
        when(ragChatService.answer("What cloud experience do you have?"))
                .thenReturn("Deployed to GCP using Terraform.");

        String result = agentTools.queryCandidateExperience("What cloud experience do you have?");

        assertThat(result).isEqualTo("Deployed to GCP using Terraform.");
    }

    @Test
    void generateCoverLetterSection_shouldFillPromptTemplateAndCallChatModel() {
        when(chatModel.generate(anyString())).thenReturn("Tailored cover letter paragraph.");

        String result = agentTools.generateCoverLetterSection("Java, Spring Boot", "5 years at EPAM building microservices");

        assertThat(result).isEqualTo("Tailored cover letter paragraph.");
        verify(chatModel).generate(contains("Java, Spring Boot"));
        verify(chatModel).generate(contains("5 years at EPAM building microservices"));
    }
}
