# AI Engineer Interview Preparation Plan

**Goal:** Prepare for an AI Engineer interview with real, runnable code samples — progressing from AI-assisted development to building production-grade AI features and agents.

**Starting point:** You already have hands-on AI experience (Claude CLI, Gemini CLI, Antigravity at Liferay) and a working Spring Boot + React project to build on.

**Mentor role:** Claude Code guides each phase, explains concepts, and reviews your implementations.

---

## Phase Overview

| Phase | Topic | Duration | Output |
|-------|-------|----------|--------|
| 1 | LLM API integration in Java | 1 week | Spring Boot endpoint that calls Claude/OpenAI |
| 2 | Prompt engineering & structured output | 1 week | Reliable, testable prompts with JSON output |
| 3 | RAG (Retrieval-Augmented Generation) | 1–2 weeks | CV-powered chatbot in the webserver project |
| 4 | AI Agents & tool use | 1–2 weeks | Agent that calls your own APIs as tools |
| 5 | Observability, evaluation, safety | 1 week | Metrics, evals, guardrails |
| 6 | Interview Q&A drill | ongoing | Confident answers to common AI engineer questions |

---

## Phase 1 — LLM API Integration in Java (Week 1)

### What to learn
- How LLM APIs work: tokens, context window, temperature, system/user/assistant roles
- Anthropic SDK vs OpenAI SDK vs LangChain4j
- Streaming responses vs blocking calls
- Cost and rate limit awareness

### Practice task
Add an `/api/ai/summarize` endpoint to `DatabaseServer` that takes a `PersonDTO` and returns an AI-generated professional summary of that person.

```java
// POST /api/ai/summarize
// Body: { "name": "Tamas Biro", "skills": ["Java", "Spring Boot", "Kafka"] }
// Returns: { "summary": "Experienced backend engineer specializing in..." }
```

### Key concepts for interviews
- **Tokens** — the unit LLMs charge and measure by; ~0.75 words per token
- **Context window** — how much text the model can "see" at once (e.g. 200k tokens for Claude)
- **Temperature** — 0 = deterministic, 1+ = creative/random; use 0 for structured tasks
- **System prompt** — sets model behavior before the conversation starts

### Deliverable
`DatabaseServer/src/main/java/com/example/ai/AiSummaryService.java` — working, tested with a real API key.

---

## Phase 2 — Prompt Engineering & Structured Output (Week 2)

### What to learn
- Few-shot prompting, chain-of-thought, role prompting
- How to reliably get JSON output from an LLM
- Prompt versioning — treat prompts like code (they deserve commits too)
- Testing prompts: why you need golden datasets

### Practice task
Build a CV skill extractor: given a raw job description text, return a structured JSON list of required skills. Integrate it so when a new job posting is pasted into the frontend, the backend extracts and stores skills.

```json
{
  "required_skills": ["Java 17", "Spring Boot", "Kafka"],
  "nice_to_have": ["Kubernetes", "AWS"],
  "seniority": "mid-level"
}
```

### Key concepts for interviews
- **Structured output / function calling** — forcing the model to return valid JSON via response format or tool schemas
- **Few-shot examples** — 2-3 examples in the prompt dramatically improve consistency
- **Prompt injection** — a security risk when user input ends up inside a prompt; how to mitigate it
- **Hallucination** — model confidently states false facts; mitigation: RAG, grounding, verification steps

### Deliverable
`DatabaseServer/src/main/java/com/example/ai/SkillExtractorService.java` + prompt templates in `resources/prompts/`.

---

## Phase 3 — RAG: Retrieval-Augmented Generation (Weeks 3–4)

### What to learn
- Why RAG exists: LLMs don't know your private data
- Embeddings — turning text into vectors that capture semantic meaning
- Vector databases: pgvector (PostgreSQL extension), Chroma, Weaviate, Pinecone
- The RAG pipeline: chunk → embed → store → retrieve → prompt → answer

### Practice task
Build a **CV chatbot** that answers questions about your own professional background, using your CV and LinkedIn profile as the knowledge base.

```
User: "What cloud experience do you have?"
Bot:  "Based on the documents, Tamas has deployed to GCP using Terraform and 
       has hands-on Kubernetes experience from multiple projects..."
```

Stack: Spring Boot + LangChain4j + pgvector (add to existing PostgreSQL).

### Architecture
```
[CV .txt / LinkedIn .txt]
        ↓  chunk (500 tokens, 50 overlap)
        ↓  embed (claude or openai embedding model)
        ↓  store in pgvector
        
[User question]
        ↓  embed question
        ↓  cosine similarity search → top 3 chunks
        ↓  inject chunks into prompt
        ↓  LLM generates answer
```

### Key concepts for interviews
- **Embedding** — a vector (array of floats) representing semantic meaning; similar text = similar vectors
- **Cosine similarity** — the standard metric for comparing embedding vectors
- **Chunking strategy** — chunk too large: noise; too small: loses context; overlap helps continuity
- **Why RAG beats fine-tuning for most cases** — cheaper, updateable without retraining, more transparent

### Deliverable
`DatabaseServer/src/main/java/com/example/ai/rag/` package with ingestion pipeline + query endpoint.

---

## Phase 4 — AI Agents & Tool Use (Weeks 5–6)

### What to learn
- What an agent is: an LLM in a loop that can call tools and decide next steps
- Tool use / function calling: how you expose your APIs to the model
- ReAct pattern: Reasoning + Acting — the model thinks before each action
- When agents go wrong: infinite loops, wrong tool calls, hallucinated parameters

### Practice task
Build a **Job Application Agent** that, given a job posting URL:
1. Fetches and parses the job description
2. Calls your CV skill extractor (Phase 2) to identify required skills
3. Queries the CV chatbot (Phase 3) to check your matching experience
4. Generates a tailored cover letter section

Tools available to the agent:
- `fetch_job_description(url)` → raw text
- `extract_skills(text)` → JSON skill list
- `query_cv(question)` → answer from RAG
- `generate_cover_letter_section(job_skills, my_experience)` → text

### Key concepts for interviews
- **Tool/function calling** — the model outputs a JSON call spec; your code executes it and returns results
- **ReAct loop** — Thought → Action → Observation → repeat until done
- **Agent vs chain** — chain: fixed sequence of steps; agent: model decides the sequence at runtime
- **Human-in-the-loop** — pausing for human approval before irreversible actions (important for production)
- **Context management** — agents accumulate conversation history; you need strategies to avoid blowing the context window

### Deliverable
`DatabaseServer/src/main/java/com/example/ai/agent/JobApplicationAgent.java`

---

## Phase 5 — Observability, Evaluation & Safety (Week 7)

### What to learn
- How to measure if your AI feature is working (evals)
- Logging LLM calls: what to capture (prompt, response, latency, tokens, cost)
- Guardrails: input/output validation, content filtering
- Responsible AI basics: bias, fairness, transparency

### Practice task
Add an **AI observability layer** to all AI endpoints:
- Log every LLM call to a database table: `ai_call_log(id, endpoint, prompt_hash, response_tokens, latency_ms, cost_usd, created_at)`
- Build a Grafana dashboard panel showing AI call volume and average latency
- Add a simple input guardrail: reject prompts containing email addresses or phone numbers (PII)

### Key concepts for interviews
- **Eval dataset** — a set of input/expected-output pairs you run against your prompts to catch regressions
- **LLM-as-judge** — using a second LLM call to score the quality of the first response
- **Prompt injection defense** — never concatenate raw user input directly into a prompt; use parameterized templates
- **Cost control** — caching identical prompts, using smaller models for simple tasks, token budgets

---

## Phase 6 — Interview Q&A Drill (Ongoing)

Work through these questions; implement a code example for any you can't answer confidently.

### Fundamentals
- What is the difference between a language model and a chat model?
- Explain transformer architecture at a high level (attention mechanism, why it matters)
- What is the difference between RAG and fine-tuning? When would you choose each?
- How do you handle hallucinations in a production system?
- What is prompt injection and how do you prevent it?

### Architecture & System Design
- Design a customer support AI system for 10,000 concurrent users
- How would you implement a document Q&A system over 1 million PDFs?
- How do you version and test prompts in a CI/CD pipeline?
- What observability would you add to an LLM-powered feature?

### Java/Spring Boot specific
- How do you call an LLM API asynchronously in Spring Boot?
- How would you implement streaming responses (SSE) from an LLM endpoint?
- How do you store and search embeddings in PostgreSQL with pgvector?
- Describe how you would integrate LangChain4j in an existing Spring Boot app

### From your own experience (Liferay)
- Tell me about the AI integrations you built at Liferay (Claude CLI, Gemini CLI, Antigravity)
- What was the most challenging part of integrating AI into an existing enterprise system?
- How did you handle security and data privacy when using AI tools at work?

---

## Technology Stack for This Plan

| Category | Technology | Why |
|----------|-----------|-----|
| LLM API | Anthropic Claude API | You already have experience; best-in-class for instruction following |
| Java AI framework | LangChain4j | Spring Boot native, actively maintained, supports RAG + agents |
| Embeddings | `text-embedding-3-small` (OpenAI) or `voyage-3-lite` (Anthropic) | Fast, cheap, good quality |
| Vector store | pgvector (PostgreSQL extension) | Reuses your existing DB, no new infra |
| Observability | Prometheus + Grafana | Already planned in your roadmap |
| Testing | JUnit 5 + WireMock | Mock LLM responses for deterministic tests |

---

## Learning Resources

- **Anthropic docs** — prompt engineering guide, tool use reference
- **LangChain4j docs** — `docs.langchain4j.dev`
- **"Building LLM Apps" by Chip Huyen** — practical, engineering-focused
- **OpenAI cookbook** — despite being OpenAI-focused, patterns are universal
- **Your own webserver project** — the primary practice ground for every phase

---

## Success Criteria

You are ready for an AI engineer interview when you can:

- [ ] Explain RAG, agents, embeddings, and tool use without notes
- [ ] Show a running demo of the CV chatbot (Phase 3)
- [ ] Show the Job Application Agent calling your own APIs (Phase 4)
- [ ] Answer the Q&A drill questions for 10 minutes without hesitation
- [ ] Describe the AI work you did at Liferay with specific technical details
- [ ] Explain one failure you encountered and how you debugged it
