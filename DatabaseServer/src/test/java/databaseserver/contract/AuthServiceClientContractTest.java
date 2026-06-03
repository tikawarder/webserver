package databaseserver.contract;

import databaseserver.services.AuthServiceClient;
import databaseserver.services.kafka.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = "auth.service.url=http://localhost:9999/api/auth/validate/"
)
@AutoConfigureStubRunner(
    ids = "authservice:AuthService:+:stubs:9999",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("test")
class AuthServiceClientContractTest {

    @MockBean
    KafkaProducerService kafkaProducerService;

    @Autowired
    AuthServiceClient authServiceClient;

    @Test
    void existingUser_shouldReturnTrue() {
        assertTrue(authServiceClient.validateUser("testuser"));
    }

    @Test
    void nonExistingUser_shouldReturnFalse() {
        assertFalse(authServiceClient.validateUser("unknown"));
    }
}
