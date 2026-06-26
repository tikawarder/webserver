package databaseserver.ai;

import databaseserver.model.dto.PersonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AiSummaryController {

    private final AiSummaryService aiSummaryService;

    public AiSummaryController(AiSummaryService aiSummaryService) {
        this.aiSummaryService = aiSummaryService;
    }

    /**
     * Generates a professional summary for the given person.
     *
     * POST /api/ai/summarize
     * Body: PersonDto JSON
     * Returns: plain text summary from the LLM
     */
    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestBody PersonDto person) {
        String summary = aiSummaryService.summarize(person);
        return ResponseEntity.ok(summary);
    }
}
