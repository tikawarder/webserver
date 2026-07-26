package databaseserver.ai.agent;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/agent")
public class JobApplicationAgentController {

    private final JobApplicationAgent jobApplicationAgent;

    public JobApplicationAgentController(JobApplicationAgent jobApplicationAgent) {
        this.jobApplicationAgent = jobApplicationAgent;
    }

    /**
     * POST /api/ai/agent/apply
     * Body: { "jobPostingUrl": "https://..." }
     *
     * The model itself decides, step by step, to fetch the posting, extract
     * skills, check matching experience, then draft a cover letter section —
     * this endpoint doesn't dictate that sequence, unlike Phase 1-3 endpoints.
     */
    @PostMapping("/apply")
    public ResponseEntity<ApplyResponse> apply(@RequestBody ApplyRequest request) {
        String result = jobApplicationAgent.helpWithApplication(request.jobPostingUrl());
        return ResponseEntity.ok(new ApplyResponse(result));
    }

    public record ApplyRequest(String jobPostingUrl) {}

    public record ApplyResponse(String result) {}
}
