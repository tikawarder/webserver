package databaseserver.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class SkillExtractorController {

    private final SkillExtractorService skillExtractorService;

    public SkillExtractorController(SkillExtractorService skillExtractorService) {
        this.skillExtractorService = skillExtractorService;
    }

    @PostMapping("/extract-skills")
    public ResponseEntity<SkillsDto> extractSkills(@RequestBody SkillExtractorRequest request) {
        SkillsDto result = skillExtractorService.extract(request.jobDescription());
        return ResponseEntity.ok(result);
    }

    public record SkillExtractorRequest(String jobDescription) {}
}
