# Skill: Explain a Feature or Concept

## Usage
`/explain [topic]`

**Mode detection — interview-answer vs. teaching:** if the topic is phrased as
a direct interview question ("mi a haszna", "mikor használnál X-et", "mit
mondanál, ha megkérdezik") rather than a request to understand/learn a concept,
answer in **interview-answer mode**: 3-5 sentences, direct, no tables, no ASCII
diagram, no code block unless one line is unavoidable — the length and shape of
an actual spoken interview answer. Reserve the full step-by-step teaching mode
below for explicit learning requests ("magyarázd el", "értsem meg alapoktól",
"mi ez és miért kell").

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

**Motivation and practical value first, internals only on request.** The reader
is preparing for interviews and for real understanding — not for a systems-internals
deep dive. Default to the "why does this exist, what does it solve, when do you
reach for it" level, anchored in a concrete scenario. Do NOT proactively go into
engine-internals territory (e.g. physical execution algorithms a query planner
picks between, wire-protocol byte layout, MVCC version-chain internals, JIT/compiler
internals) unless the reader explicitly asks to go deeper ("menjünk mélyebbre",
"hogy működik motorháztető alatt", "miért pont ezt választja a motor"). If in
doubt whether something is "practical value" or "internals", default to leaving
it out and offer it as an optional next step instead of including it inline.

**Build on what's already understood.** If this explanation continues a session
where earlier concepts were already covered (check the conversation), open by
naming which prior concept this one builds on, and what problem/limitation in
that prior concept is what makes the new one necessary. Never introduce a new
topic in a vacuum when a natural predecessor was just covered.

## Steps

1. **Identify the topic and its category**
   - AI/LLM concept → primary source is `AI/AI_Engineer_Interview_Prep.md`
   - General backend/Java/Spring/microservices concept → primary source is
     `microservices_roadmap.md`, `docs/branches.md`, or general knowledge
   - A specific class/file in this codebase → find and read it first
   - If ambiguous, ask for clarification

2. **Motivation — max 4-5 sentences**
   - What problem/gap existed before this concept, or what breaks/what you'd
     lose without it — this is the "why did this even come up" framing, not a
     definition-first opening
   - If it builds on an already-covered concept in this session, say so explicitly
     ("eddig X-et néztük, ennek ez volt a korlátja, ezért kell Y")
   - One mechanical-engineering-flavored analogy is welcome, but keep it tied to
     the motivation, not to internal mechanism

3. **One practical example — code + what it buys you**
   - Paste the real snippet: if the concept already exists in this codebase, copy
     it verbatim with a `file:line` reference; if it doesn't exist yet, write a
     short illustrative Java/code snippet
   - Walk through it at the level of "what changes for you because of this code" —
     a concrete before/after or a real scenario where it matters — not a
     mechanism-by-mechanism internals breakdown
   - Stay at ONE example. Do not stack a second illustrative variant unless asked

3b. **Text/ASCII diagram (always include, directly in the terminal output)**
   - Right after the example, add a small ASCII diagram (boxes and arrows,
     `-->`, `|`, simple box-drawing) showing the practical flow just walked
     through — e.g. before/after states, a request path, a pipeline of steps
   - Plain text only, printed inline in the response — never use the `Artifact`
     tool or any browser-based rendering for this
   - Diagram the practical flow, not the engine's internal execution mechanism

4. **QA-angle close (2-3 sentences, only if it adds something)**
   - Why is it done this way — what's the alternative or what came before it?
   - What would break, or what would you lose, if you did it the naive/other way?
   - One clear trade-off (pro/con) — skip this step entirely if there's no real
     alternative worth contrasting, don't force it

5. **(Only if asked about interview context) Interview angle**
   - The typical interview question testing this concept
   - The answer a mid-level (Medior) developer is expected to give
   - One likely follow-up question

6. **End with an offer, not an assumption**
   - Close with a short, concrete offer to go deeper on ONE specific named
     aspect (e.g. "ha érdekel, hogyan dönt a motor Nested Loop és Hash Join
     között, szólj") instead of unprompted diving into that depth

## Tone
Follow [learning-context.md](../rules/learning-context.md): explain in Hungarian,
stop after the explanation rather than immediately moving to the next task, and
avoid AI-sounding filler — direct, concrete, no empty superlatives.
