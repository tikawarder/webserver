package databaseserver.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Golden-dataset eval for SkillExtractorService: runs the REAL LLM (no @MockBean) against a
 * fixed set of job postings with known-good expected skills, and asserts a minimum pass rate.
 * Unlike SkillExtractorServiceTest (mocked, deterministic), this measures whether the actual
 * prompt still performs on realistic input — the thing that silently regresses when the prompt
 * or model changes. Costs real tokens, so it only runs when explicitly requested.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "AI_INTEGRATION_TEST", matches = "true")
class SkillExtractorEvalIT {

    private static final Logger log = LoggerFactory.getLogger(SkillExtractorEvalIT.class);
    private static final double MIN_PASS_RATE = 0.7;

    @Autowired
    SkillExtractorService skillExtractorService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void extract_shouldMeetMinimumPassRateAcrossGoldenDataset() throws Exception {
        List<EvalCase> cases = loadCases();

        long passed = 0;
        for (EvalCase evalCase : cases) {
            SkillsDto result = skillExtractorService.extract(evalCase.jobDescription());
            boolean pass = result.requiredSkills().containsAll(evalCase.expectedRequiredSkills());
            log.info("[EVAL] pass={} expected={} actual={}", pass, evalCase.expectedRequiredSkills(), result.requiredSkills());
            if (pass) {
                passed++;
            }
        }

        double passRate = (double) passed / cases.size();
        log.info("[EVAL] pass rate: {}/{} = {}", passed, cases.size(), passRate);

        assertThat(passRate).isGreaterThanOrEqualTo(MIN_PASS_RATE);
    }

    private List<EvalCase> loadCases() throws Exception {
        try (InputStream input = new ClassPathResource("eval/skill-extraction-eval-cases.json").getInputStream()) {
            return objectMapper.readValue(input,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, EvalCase.class));
        }
    }
}
