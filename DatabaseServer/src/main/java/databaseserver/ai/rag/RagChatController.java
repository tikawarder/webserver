package databaseserver.ai.rag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/rag-chat")
public class RagChatController {

    private final RagIngestionService ingestionService;
    private final RagChatService chatService;

    public RagChatController(RagIngestionService ingestionService, RagChatService chatService) {
        this.ingestionService = ingestionService;
        this.chatService = chatService;
    }

    /**
     * Re-reads rag/kubernetes-overview.txt, re-chunks and re-embeds it into rag_chunks.
     * Call this once at startup and again whenever the source document changes.
     *
     * POST /api/ai/rag-chat/ingest
     */
    @PostMapping("/ingest")
    public ResponseEntity<IngestResponse> ingest() {
        int chunkCount = ingestionService.ingest();
        return ResponseEntity.ok(new IngestResponse(chunkCount));
    }

    /**
     * POST /api/ai/rag-chat
     * Body: { "question": "What is the Kubernetes control plane responsible for?" }
     */
    @PostMapping
    public ResponseEntity<AnswerResponse> chat(@RequestBody QuestionRequest request) {
        String answer = chatService.answer(request.question());
        return ResponseEntity.ok(new AnswerResponse(answer));
    }

    public record QuestionRequest(String question) {}

    public record AnswerResponse(String answer) {}

    public record IngestResponse(int chunksStored) {}
}
