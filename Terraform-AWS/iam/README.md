# terraform-deployer least-privilege policy

`terraform-deployer` currently has `AdministratorAccess`. This directory holds a
scoped-down replacement, built by listing every `resource` type Terraform actually
declares (`grep -ho '^resource "[a-z_]*"' *.tf`) and mapping each to the IAM actions
it needs — instead of guessing.

## Why `Resource: "*"` on most statements, not tighter ARNs

Several AWS services (EC2/VPC, ECS, Auto Scaling) don't support resource-level
permissions for their create/describe actions at all — this isn't a shortcut, it's
a hard IAM limitation for those APIs. Where AWS *does* support scoping (ECR
repository names, CloudWatch log group names, IAM role/instance-profile names),
this policy narrows to the project's `webserver-learning*` naming prefix.

## The important part: `iam:PassRole`

`PassRole` is the one action in this whole policy that can silently turn "scoped"
into "unscoped" if handled carelessly.

When Terraform creates an EC2 instance with `iam_instance_profile = aws_iam_instance_profile.ssm.name`,
it isn't the EC2 instance asking IAM for that role — **the calling identity**
(`terraform-deployer`) has to have permission to *hand that role to* the EC2
service. That hand-off is `iam:PassRole`. If it's granted with `Resource: "*"`,
then `terraform-deployer` could pass *any* role in the account to *any* service —
including, say, an existing highly-privileged role nothing here is supposed to
touch. A user with `iam:PassRole: *` plus permission to launch an EC2 instance is,
in effect, one `RunInstances` call away from acting as whatever role it passes.

This policy fixes that two ways at once:
- `Resource` restricted to `webserver-learning-*` roles only — can't pass any role
  outside this project.
- `Condition: iam:PassedToService` restricted to `ec2.amazonaws.com` and
  `ecs-tasks.amazonaws.com` — can't pass even a `webserver-learning-*` role to an
  unrelated service in some future misuse.

## The bootstrap problem — why this isn't a Terraform resource

`terraform-deployer` would need permission to modify its own IAM policy before it
has that permission — a chicken-and-egg problem. The standard real-world pattern
splits this: a separate, more-privileged "bootstrap" identity (or a human via the
console) manages the deployer's own permissions; the deployer itself never touches
IAM policies attached to IAM *users*, only to the roles it creates for AWS
services to assume.

## How to apply (manual, one-time)

```bash
aws iam create-policy \
  --policy-name webserver-learning-deployer-policy \
  --policy-document file://terraform-deployer-policy.json \
  --profile <a profile with IAM admin rights>

aws iam attach-user-policy \
  --user-name terraform-deployer \
  --policy-arn arn:aws:iam::<account-id>:policy/webserver-learning-deployer-policy \
  --profile <a profile with IAM admin rights>

aws iam detach-user-policy \
  --user-name terraform-deployer \
  --policy-arn arn:aws:iam::aws:policy/AdministratorAccess \
  --profile <a profile with IAM admin rights>
```

Then run `terraform plan` with the `terraform-deployer` profile — if any action is
missing from the policy, AWS returns an `AccessDenied` error naming the exact
action, which is the fastest way to find gaps.
