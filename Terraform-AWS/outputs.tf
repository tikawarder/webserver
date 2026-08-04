output "public_ip" {
  description = "Elastic IP — the app will be reachable at http://<this-ip>:9080 once docker compose is up"
  value       = aws_eip.app_server.public_ip
}

output "instance_id" {
  description = "Use with: aws ssm start-session --target <instance_id>"
  value       = aws_instance.app_server.id
}
