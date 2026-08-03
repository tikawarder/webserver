package databaseserver.ai.observability;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromptGuardrailTest {

    private final PromptGuardrail guardrail = new PromptGuardrail();

    @Test
    void assertNoPii_shouldRejectEmailAddress() {
        assertThatThrownBy(() -> guardrail.assertNoPii("Contact me at tamas.biro@example.com for details."))
                .isInstanceOf(PiiDetectedException.class)
                .hasMessageContaining("email");
    }

    @Test
    void assertNoPii_shouldRejectPhoneNumber() {
        assertThatThrownBy(() -> guardrail.assertNoPii("Call me on +36 30 123 4567 anytime."))
                .isInstanceOf(PiiDetectedException.class)
                .hasMessageContaining("phone");
    }

    @Test
    void assertNoPii_shouldAllowOrdinaryJobDescriptionText() {
        assertThatCode(() -> guardrail.assertNoPii(
                "We are looking for a Java developer with 5+ years of Spring Boot experience."))
                .doesNotThrowAnyException();
    }
}
