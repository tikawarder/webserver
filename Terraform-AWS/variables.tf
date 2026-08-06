variable "aws_region" {
  description = "AWS region — eu-central-1 (Frankfurt) chosen for lower latency from Hungary; costs slightly more than us-east-1 (~10-15% on EC2 on-demand rates)"
  type        = string
  default     = "eu-central-1"
}

variable "instance_type" {
  description = "t3.micro (1GB) OOM-killed Keycloak under the 8 infra containers; t3.medium (4GB) fits them all now that the paid-tier account removes the Free Tier instance-type restriction"
  type        = string
  default     = "t3.medium"
}

variable "gemini_api_key" {
  description = "Real key is injected via local.gemini_api_key (read from the gitignored ../AI/.env); this default is only a placeholder for when that file is absent."
  type        = string
  sensitive   = true
  default     = "not-configured"
}

variable "app_port" {
  description = "Port the React app (UserInputServer/nginx) listens on, per docker-compose.yml"
  type        = number
  default     = 9080
}

variable "repo_url" {
  description = "Git URL cloned by the instance's startup script. Leave as-is for the public repo, or override for a fork."
  type        = string
  default     = "https://github.com/tikawarder/webserver"
}

variable "repo_branch" {
  description = "Branch to check out on the instance — must exist on the remote (origin), not just locally"
  type        = string
  default     = "AWS"
}

variable "app_instance_type" {
  description = "Instance type for the ECS container instance running the 6 self-built services. t3.micro couldn't fit all 6 simultaneously (each reserves 300MB, 6x exceeds the ~950MB usable on a 1GB box) and the original account's Free Tier restriction blocked any larger type. The new paid-tier account removes that restriction, so t3.medium (4GB) runs all 6 comfortably."
  type        = string
  default     = "t3.medium"
}

variable "enabled_services" {
  description = "Subset of ecs_services actually scheduled (desired_count = 1) — the rest stay defined but scaled to 0. All 6 enabled now that app_instance_type is t3.medium (4GB), which fits all 6 JVMs even at their original per-service memory sizing."
  type        = list(string)
  default     = []
}

variable "ecs_services" {
  description = "The 6 self-built services, each getting an ECR repository, task definition, and ECS service. memory is the hard docker limit (MB) — the 5 JVMs kept OOM-killing (exit 137) at the old t3.micro-era values even after moving to t3.medium, because this per-task limit is independent of host size; raised generously now that the host has ~3.5GB to spare. userinput-server is plain nginx (no JVM) so it gets much less."
  type = map(object({
    context_dir    = string # not used by Terraform directly — documents the docker build context for build-and-push.sh
    container_port = number
    host_port      = number
    memory         = number
  }))
  default = {
    auth-service = {
      context_dir    = "AuthService"
      container_port = 8083
      host_port      = 9083
      memory         = 400
    }
    database-server = {
      context_dir    = "DatabaseServer"
      container_port = 8081
      host_port      = 9081
      memory         = 500
    }
    notification-service = {
      context_dir    = "NotificationService"
      container_port = 8082
      host_port      = 9082
      memory         = 350
    }
    gateway = {
      context_dir    = "GatewayService"
      container_port = 8090
      host_port      = 9090
      memory         = 400
    }
    userinput-server = {
      context_dir    = "UserInputServer"
      container_port = 8080
      host_port      = 9080
      memory         = 100
    }
    reactive-service = {
      context_dir    = "ReactiveService"
      container_port = 8084
      host_port      = 9084
      memory         = 400
    }
  }
}
