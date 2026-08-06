#!/bin/bash
# Builds and pushes the 6 self-built services to their ECR repositories.
# Run this locally (not on the ECS instance) — the whole point of phase 2 is
# that building never happens on the constrained AWS instance.
#
# Usage: AWS_PROFILE=terraform-deployer ./build-and-push.sh
set -euo pipefail

REGION="${AWS_DEFAULT_REGION:-eu-central-1}"
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REGISTRY="${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com"

declare -A SERVICES=(
  [auth-service]="AuthService"
  [database-server]="DatabaseServer"
  [notification-service]="NotificationService"
  [gateway]="GatewayService"
  [userinput-server]="UserInputServer"
  [reactive-service]="ReactiveService"
)

echo "Logging in to ${REGISTRY}..."
aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$REGISTRY"

for name in "${!SERVICES[@]}"; do
  context_dir="${REPO_ROOT}/${SERVICES[$name]}"
  repo="webserver-learning/${name}"
  echo ""
  echo "=== Building ${name} (${context_dir}) ==="
  docker build -t "${repo}:latest" "$context_dir"
  docker tag "${repo}:latest" "${REGISTRY}/${repo}:latest"
  echo "=== Pushing ${name} ==="
  docker push "${REGISTRY}/${repo}:latest"
done

echo ""
echo "All 6 images pushed. Next: terraform apply (or force new ECS deployments if the services already exist):"
echo "  for svc in ${!SERVICES[@]}; do aws ecs update-service --cluster webserver-learning-cluster --service \$svc --force-new-deployment --region $REGION; done"
