# AWS fundamentals — lifting the webserver stack to the cloud (Phase 1)

Learning goal: practice AWS fundamentals (VPC, EC2, Security Group, IAM, SSM) by
running the existing `docker-compose.yml` stack on a single Free Tier EC2 instance —
a similar shape to the GCP setup in `Terraform/`, but on AWS, using only free
components.

## Why it's structured this way

- **No EKS, no Fargate/App Runner** — neither has a (sufficient) free tier. Instead,
  a single `t3.micro` EC2 instance runs the whole stack via Docker Compose.
- **No NAT Gateway** — never free. The instance sits in a public subnet instead,
  with only the necessary ports open on the Security Group.
- **No open port 22 (SSH)** — administration goes through AWS SSM Session Manager,
  which needs no inbound port at all.
- **Dedicated IAM user for Terraform** (`terraform-deployer`), not the root account.
  Currently granted `AdministratorAccess` for simplicity (personal learning account,
  not a shared/production one) rather than a scoped policy.

## Prerequisites (one-time, at account setup)

1. AWS account (Free Tier — 750 hours/month of `t2/t3.micro` for 12 months, for new
   accounts)
2. **AWS Budget / zero-spend alert** configured (Billing → Budgets → "Zero spend
   budget" template) — already done, email alert above $0.01
3. Dedicated IAM user (`terraform-deployer`) created, access key generated, configured
   locally: `aws configure --profile terraform-deployer`

## Usage

```bash
cd Terraform-AWS
AWS_PROFILE=terraform-deployer terraform init
AWS_PROFILE=terraform-deployer terraform plan
AWS_PROFILE=terraform-deployer terraform apply
```

`AWS_PROFILE` is passed as an environment variable rather than hardcoded in the
code, so it works on any machine regardless of local profile naming.

After `apply`:
- `terraform output public_ip` — the Elastic IP; the app will be reachable at
  `http://<ip>:9080`
- `terraform output instance_id` — use this to connect via SSM:
  ```bash
  AWS_PROFILE=terraform-deployer aws ssm start-session --target <instance_id>
  ```
- On first boot, `user_data` automatically installs Docker/Docker Compose and
  clones the repo — if the repo is private, this step silently fails, and cloning
  needs to be done manually over SSM, followed by `docker compose up -d`.

## Mandatory discipline: always `destroy` after a session

```bash
AWS_PROFILE=terraform-deployer terraform destroy
```

Unlike GCP's `e2-micro` "Always Free" guarantee, the AWS Free Tier only applies
for **12 months, to new accounts**. Leaving the instance running after a session
serves no purpose even within the Free Tier — the discipline: always destroy it
when not in active use.

## Next phases (roadmap, not built yet)

- ECS Cluster (EC2 launch type) + separate task definitions for backend/frontend —
  the counterpart to the GCP Cloud Run split
- RDS (Free Tier `db.t3.micro`) instead of the self-hosted database container
