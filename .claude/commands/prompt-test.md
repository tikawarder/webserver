# Skill: Test a Prompt Against the Live API

## Usage
`/prompt-test [prompt-file]`

Example: `/prompt-test prompts/extract-skills-v1.txt`

If no file is given, list available prompt files under `DatabaseServer/src/main/resources/prompts/`.

## Steps

1. **Load the prompt file**
   - Read the specified file from `DatabaseServer/src/main/resources/prompts/[file]`
   - Identify placeholder variables (e.g. `{{job_description}}`, `{{person_name}}`)
   - Show the prompt to the user before running

2. **Ask for test input**
   - If the prompt has placeholders, ask the user to provide values, or use a sensible default:
     - `{{job_description}}` → use a short sample Java engineer job posting
     - `{{person_name}}` → use "Tamas Biro"
     - `{{skills}}` → use "Java, Spring Boot, Kafka, Docker"

3. **Run the prompt**
   - Call the Claude API directly using the prompt with substituted values
   - Use temperature 0 for deterministic output
   - Print the raw response

4. **Evaluate the output**
   - Does the response match the expected format?
   - If JSON output was expected: is it valid JSON? Does it have all required fields?
   - Any hallucinations or unexpected content?

5. **Suggest improvements**
   - If output quality is poor: suggest a specific prompt change (add few-shot example, tighten the instruction, add output schema)
   - Save improved version as `[filename]-v2.txt` — never overwrite the previous version
   - Explain what prompt engineering technique was applied and why it helps

6. **Interview concept link**
   - Name the technique used (few-shot, chain-of-thought, structured output, etc.)
   - Explain when you would use it in a production system
