# Skill: Start an AI Learning Phase

## Usage
`/ai-phase-start [1-6]`

Example: `/ai-phase-start 1`

## Steps

1. **Read the plan**
   - Load `AI/AI_Engineer_Interview_Prep.md`
   - Identify the requested phase: title, goal, practice task, deliverable

2. **Inspect current state**
   - Check if `DatabaseServer/src/main/java/databaseserver/ai/` exists
   - Check `DatabaseServer/pom.xml` for existing AI-related dependencies (langchain4j, anthropic, openai)
   - Check `DatabaseServer/src/main/resources/prompts/` for existing prompt files
   - Run `git log --oneline -5` to see recent work

3. **Explain the phase** (before writing any code)
   - What is the core concept being learned?
   - Why does it matter for an AI engineer interview?
   - What real-world systems use this pattern?
   - What is the practice task and expected deliverable?

4. **Implement the code step by step — Claude writes everything**
   - Create the necessary package directories under `databaseserver/ai/`
   - Add required dependencies to `DatabaseServer/pom.xml` if missing
   - Write the **full, working implementation** — no TODOs, no skeletons, no "implement this yourself"
   - For each class, explain what it does and why BEFORE writing it
   - Create prompt template files under `DatabaseServer/src/main/resources/prompts/` if needed

   Phase-specific files:
   - **Phase 1** — `ai/AiConfig.java`, `ai/AiSummaryController.java`, `ai/AiSummaryService.java`
   - **Phase 2** — `ai/SkillExtractorService.java`, `resources/prompts/extract-skills-v1.txt`
   - **Phase 3** — `ai/rag/EmbeddingService.java`, `ai/rag/RagQueryService.java`, `ai/rag/RagController.java`
   - **Phase 4** — `ai/agent/JobApplicationAgent.java`, `ai/agent/AgentTool.java`
   - **Phase 5** — `ai/observability/AiCallLog.java`, `ai/observability/AiCallLogRepository.java`, `ai/observability/AiLoggingAspect.java`
   - **Phase 6** — no new code; open `AI/AI_Engineer_Interview_Prep.md` Q&A section and drill

5. **Wrap up with interview prep**
   - Summarize what concepts were demonstrated in this phase
   - Give 2-3 interview questions the user should now be able to answer
   - Suggest the next command: `/interview-drill [N]` or `/ai-phase-start [N+1]`
