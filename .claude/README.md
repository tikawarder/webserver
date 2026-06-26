# Claude Code Configuration

This directory configures how Claude behaves in this project.

## Structure

```
.claude/
├── README.md           ← this file
├── settings.local.json ← permissions and tool allowlist
├── rules/              ← always active, loaded every session automatically
│   ├── coding-standards.md    — English-only code, no commits without permission
│   ├── learning-context.md    — learning project context, Strangler Fig pattern
│   ├── ai-api-keys.md         — where API keys live, default models (fill in when ready)
│   └── ai-coding-patterns.md  — LangChain4j conventions, prompt file location, WireMock
└── commands/           ← slash commands, invoked manually with /name
    ├── learning-checkpoint.md  → /learning-checkpoint
    ├── microservice-extract.md → /microservice-extract [ServiceName]
    ├── k8s-convert.md          → /k8s-convert
    ├── ai-phase-start.md       → /ai-phase-start [1-6]
    ├── ai-checkpoint.md        → /ai-checkpoint
    ├── explain.md              → /explain [concept]
    ├── interview-drill.md      → /interview-drill [phase]
    ├── ai-add-test.md          → /ai-add-test [ClassName]
    └── prompt-test.md          → /prompt-test [file]
```

## Rules vs Commands

| | Rules | Commands |
|---|---|---|
| Location | `rules/` | `commands/` |
| Activation | Automatic — every session | Manual — you type `/name` |
| Purpose | Background constraints and conventions | Step-by-step workflows |
| Example | "never commit without permission" | `/explain RAG` walks through a full concept explanation |

## Available Commands

### General learning
- `/learning-checkpoint` — compare current codebase state against the microservices roadmap
- `/microservice-extract [ServiceName]` — scaffold a new microservice from the monolith (Strangler Fig)
- `/k8s-convert` — convert docker-compose.yml to Kubernetes manifests

### AI interview preparation
- `/ai-phase-start [1-6]` — scaffold code and explain concepts for a given phase of the AI prep plan
- `/ai-checkpoint` — show progress across all 6 AI learning phases
- `/explain [concept]` — explain an AI concept in 3 layers + interview angle (e.g. `/explain RAG`)
- `/interview-drill [phase]` — interactive Q&A drill with scoring and model answers
- `/ai-add-test [ClassName]` — generate WireMock-based unit tests for an AI service class
- `/prompt-test [file]` — run a prompt file against the live API and suggest improvements

## The AI learning plan

The full 6-phase interview preparation roadmap is at:
`AI/AI_Engineer_Interview_Prep.md`

Phases: LLM API → Prompt Engineering → RAG → Agents → Observability → Interview Drill
