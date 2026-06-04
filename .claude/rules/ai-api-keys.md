# AI API Keys & Configuration

## Environment variables
<!-- Fill in when you have API keys set up -->
<!-- Example:
- `ANTHROPIC_API_KEY` — Claude API, stored in `.env` (never commit this file)
- `OPENAI_API_KEY` — OpenAI / embedding model, same `.env`
-->

## Where to find them
- Local: `.env` file in project root (gitignored)
- CI/CD: GitHub Actions secrets (Settings → Secrets)

## Default model choices
<!-- Fill in your preferred models once decided -->
<!-- Example:
- Chat/completion: `claude-sonnet-4-6`
- Embeddings: `text-embedding-3-small`
-->

## Cost awareness
- Always use `temperature=0` for structured output tasks (deterministic + cheaper)
- Log token usage in dev so you can spot runaway prompts early
