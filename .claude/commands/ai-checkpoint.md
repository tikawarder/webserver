# Skill: AI Interview Prep Checkpoint

## Usage
`/ai-checkpoint`

## Steps

1. **Load the plan**
   - Read `AI/AI_Engineer_Interview_Prep.md` — the 6-phase roadmap

2. **Inspect the codebase**
   - Check which packages exist under `databaseserver/ai/`
   - List all files under `DatabaseServer/src/main/resources/prompts/`
   - Check `DatabaseServer/pom.xml` for langchain4j, anthropic, or openai dependencies
   - Run `git log --oneline -15` to see recent AI-related commits
   - Check `AI/` folder for notes and eval files

3. **Map findings to phases**
   - Phase 1 (LLM API): is `AiSummaryService` implemented and tested?
   - Phase 2 (Prompt engineering): is `SkillExtractorService` implemented? Are prompt files versioned?
   - Phase 3 (RAG): does `rag/` package exist? Is pgvector configured?
   - Phase 4 (Agents): does `agent/` package exist? Are tools defined?
   - Phase 5 (Observability): is `AiCallLog` table in use? Is there a Grafana panel?
   - Phase 6 (Q&A drill): any notes in `AI/notes/`?

4. **Generate the checkpoint report**
   ```
   === AI INTERVIEW PREP CHECKPOINT ===

   COMPLETED:
   - [phases or tasks done, with file references]

   IN PROGRESS:
   - [current phase and what's left]

   NOT STARTED:
   - [remaining phases]

   NEXT STEP:
   - [single most important next action with file path]

   INTERVIEW READINESS:
   - Concepts you can explain now: [list]
   - Concepts still to practice: [list]

   Estimated progress: [X/6 phases complete]
   ```

5. **Recommend the next action**
   - Call `/ai-phase-start [N]` for the next incomplete phase, or
   - Call `/prompt-test` if a prompt file exists but hasn't been tested yet
