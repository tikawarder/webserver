package databaseserver.ai.observability;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/*
 * Input guardrail: rejects free-text user input that looks like it contains an
 * email address or a phone number, before that text is interpolated into a
 * prompt. This is a simple regex heuristic, not a real PII detector — a
 * production system would use a dedicated NER-based tool (e.g. Microsoft
 * Presidio, AWS Comprehend) to avoid both false negatives (obfuscated PII) and
 * false positives (this regex will flag things like "2020-2024" as a phone
 * number). Good enough to demonstrate the guardrail pattern: never let raw
 * user input reach an LLM prompt unchecked.
 */
@Component
public class PromptGuardrail {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(?<!\\d)(\\+?\\d[\\d\\-.\\s()]{6,}\\d)(?!\\d)");

    public void assertNoPii(String text) {
        if (EMAIL_PATTERN.matcher(text).find()) {
            throw new PiiDetectedException("Input rejected: looks like it contains an email address.");
        }
        if (PHONE_PATTERN.matcher(text).find()) {
            throw new PiiDetectedException("Input rejected: looks like it contains a phone number.");
        }
    }
}
