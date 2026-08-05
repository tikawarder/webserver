output "infra_public_ip" {
  description = "Infra EC2 Elastic IP — Keycloak/Zipkin/Grafana/Prometheus dashboards, and the private IP the ECS tasks connect to"
  value       = aws_eip.infra_server.public_ip
}

output "infra_private_ip" {
  description = "Infra EC2 private IP — used by ECS task definitions to reach postgres_db/keycloak/kafka/zipkin/redis"
  value       = aws_instance.infra_server.private_ip
}

output "infra_instance_id" {
  description = "Use with: aws ssm start-session --target <instance_id>"
  value       = aws_instance.infra_server.id
}

output "app_public_ip_lookup_command" {
  description = "The ECS container instance is created by an Auto Scaling Group, so its public IP isn't known until it launches — run this after apply to find it"
  value       = "aws ec2 describe-instances --profile terraform-deployer --region ${var.aws_region} --filters Name=tag:Name,Values=webserver-learning-app-instance Name=instance-state-name,Values=running --query 'Reservations[].Instances[].PublicIpAddress' --output text"
}

output "ecr_repository_urls" {
  description = "Push built images here (see build-and-push.sh)"
  value       = { for k, r in aws_ecr_repository.services : k => r.repository_url }
}
