# Skill: Learning Progress Checkpoint

## Usage
`/learning-checkpoint`

## Steps

1. **Read the roadmap and development plan**
   - Load `microservices_roadmap.md` — the 8-step weekly plan
   - Load `Szakmai_Fejlodesi_Terv.md` — the overall Medior developer goal

2. **Inspect the current codebase state**
   - Run `git log --oneline -20` to see recent commits
   - Check what services exist beyond DatabaseServer and UserInputServer
   - Check if a `k8s/` directory exists
   - Check if a `helm/` directory exists
   - Check `.github/workflows/` for CI/CD maturity
   - Check `Terraform/` for IaC progress

3. **Compare against the roadmap phases**
   - Phase 1 (API Gateway): done / in progress / not started?
   - Phase 2 (Resilience + Redis): done / in progress / not started?
   - Phase 3 (Local Kubernetes): done / in progress / not started?
   - Phase 4 (CI/CD + GitOps): done / in progress / not started?
   - Phase 5 (Observability): done / in progress / not started?
   - Phase 6 (AWS + Terraform): done / in progress / not started?

4. **Generate the checkpoint report**
   ```
   === LEARNING CHECKPOINT ===

   COMPLETED:
   - [list of done items with brief notes]

   IN PROGRESS:
   - [current work]

   NEXT STEP:
   - [single most important next action, with file/command]

   GAPS vs. Medior Java target:
   - [what's still missing for the CV/job market goal]

   Estimated progress: [X/8 roadmap phases complete]
   ```

5. **Suggest the next concrete action**
   - One specific task, with the relevant file path or command to start
