This is a personal learning project — a continuously evolving web application built to reach Medior Java Developer level.
CV and professional background data is available at: /home/me/Documents/cv/ — useful context for what is already known and what still needs to be learned.

All code and comments must be written in English only.
Never commit code unless explicitly asked to do so.

## Explanations during learning
When working through a multi-step implementation (e.g. Flyway + Optimistic Locking), stop after each important topic or milestone and explain clearly in Hungarian what was done and why it matters — before moving on to the next step.

## Code comments
Default to no comments. Only add one when the WHY is genuinely non-obvious to a
medior/senior Java developer — a hidden constraint, a subtle invariant, a
workaround, or behavior that would surprise a reader (e.g. why `@Transactional`
is deliberately absent somewhere, why a propagation level was chosen). Never
comment what the code already says on its own (e.g. `// build the request`
above a line that visibly builds a request), never restate a method/annotation
name in prose, and never reference the current task or a past bug by name —
that belongs in the PR description, not the code. The milestone explanation
belongs in the chat response, not in code comments.
