# Skill: Explain a Feature or Concept

## Usage
`/explain [topic]`

Examples:
- `/explain RAG` (AI concept)
- `/explain optimistic locking` (general backend concept)
- `/explain circuit breaker` (Resilience4j / microservices concept)
- `/explain AiCallLoggingListener` (a specific class/feature in this codebase)
- `/explain @Async` (a Java/Spring mechanism)

Works for anything: an AI/LLM concept, a general software engineering pattern, a
Spring/Java mechanism, or a specific piece of code already in this project.

## Audience calibration (always applies)
The reader has a **mechanical engineering background**, 5 years of hands-on Java,
and strong logical/systems thinking — but no CS degree. Do not dumb down the
depth or the precision of the explanation. Instead:
- Prefer analogies from **physical/mechanical systems** (tolerances, feedback loops,
  control systems, pipelines, load paths, wear/fatigue, gear ratios) over generic
  "imagine you're at a restaurant" analogies — these map faster for this reader.
- Ground anything that assumes unstated CS-degree background (e.g. formal automata
  theory, unexplained Big-O notation) in a concrete example instead of naming the
  abstract theory.

**Keep it tight.** The reader explicitly does not want the explanation "watered
down" with extra sections. Do not add an interview-angle or common-mistakes
section unless the topic is explicitly asked about in an interview context
(e.g. `/explain circuit breaker interview`) — otherwise stop after step 3.

## Steps

1. **Identify the topic and its category**
   - AI/LLM concept → primary source is `AI/AI_Engineer_Interview_Prep.md`
   - General backend/Java/Spring/microservices concept → primary source is
     `microservices_roadmap.md`, `docs/branches.md`, or general knowledge
   - A specific class/file in this codebase → find and read it first
   - If ambiguous, ask for clarification

2. **Give a short intro — max 5 sentences**
   - What it is and what problem it solves, plus one mechanical-engineering-flavored
     analogy (tolerance, feedback loop, control system, load path, gear ratio, etc.)
   - No layered sub-headings here — just a tight paragraph

3. **Show the actual code, then explain it**
   - Paste the real snippet: if the concept already exists in this codebase, copy
     it verbatim with a `file:line` reference; if it doesn't exist yet, write a
     short illustrative Java/code snippet
   - Walk through the pasted code block by block — what happens and why it's
     written that way — anchored to the actual lines, not abstract description

3b. **Text/ASCII diagram (always include, directly in the terminal output)**
   - Right after the code walkthrough, add a small ASCII diagram (boxes and
     arrows, `-->`, `|`, simple box-drawing) showing the flow or structure of
     the concept — e.g. request/response path, before/after states, a pipeline
     of steps
   - Plain text only, printed inline in the response — never use the `Artifact`
     tool or any browser-based rendering for this
   - Keep it to the actual mechanism just explained, not a generic textbook
     diagram — it should visualize the specific code/flow just walked through

4. **QA-angle close (2-3 sentences, only if it adds something)**
   - Why is it done this way — what's the alternative or what came before it?
   - What would break, or what would you lose, if you did it the naive/other way?
   - One clear trade-off (pro/con) — skip this step entirely if there's no real
     alternative worth contrasting, don't force it

5. **(Only if asked about interview context) Interview angle**
   - The typical interview question testing this concept
   - The answer a mid-level (Medior) developer is expected to give
   - One likely follow-up question

## Tone
Follow [learning-context.md](../rules/learning-context.md): explain in Hungarian,
stop after the explanation rather than immediately moving to the next task, and
avoid AI-sounding filler — direct, concrete, no empty superlatives.
