# Skill: AI Interview Drill

## Usage
`/interview-drill [phase]`

Examples:
- `/interview-drill 1` — questions about LLM API integration
- `/interview-drill 3` — questions about RAG
- `/interview-drill` — random mix across all phases

## Steps

1. **Select questions**
   - Load `AI/AI_Engineer_Interview_Prep.md`, Phase 6 Q&A section
   - If a phase is specified, focus on questions relevant to that phase
   - Pick 3–5 questions; mix conceptual and practical

2. **Ask one question at a time**
   - Present the question clearly
   - Wait for the user's answer before continuing
   - Do not give hints unless asked

3. **Evaluate the answer**
   For each answer, give structured feedback:
   ```
   SCORE: [1-5]

   WHAT WAS GOOD:
   - [specific strengths]

   MISSING / IMPROVE:
   - [what a strong answer would have included]

   MODEL ANSWER:
   [a concise, interview-ready answer]
   ```

4. **After all questions**
   - Give an overall summary: which concepts are solid, which need more practice
   - Suggest the relevant `/explain [concept]` or `/ai-phase-start [N]` as next step

## Scoring guide
- 5 — complete, precise, could say this live without notes
- 4 — correct but missing one key detail
- 3 — right direction but vague or incomplete
- 2 — partial understanding, significant gaps
- 1 — incorrect or confused
