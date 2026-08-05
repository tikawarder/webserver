variable "aws_region" {
  description = "AWS region — Free Tier applies in every region, us-east-1 kept for consistency with the GCP setup's us-east1"
  type        = string
  default     = "us-east-1"
}

variable "instance_type" {
  description = "Free Tier eligible for the first 12 months of a new AWS account"
  type        = string
  default     = "t3.micro"
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
  default     = "master"
}

variable "app_instance_type" {
  description = "Instance type for the ECS container instance running the 6 self-built services. t3.micro can't fit all 6 simultaneously (each reserves 300MB, 6x exceeds the ~950MB usable on a 1GB box). t3.medium would fix that, but this account's Free Tier restriction blocks launching any non-free-tier-eligible type — so we stay on t3.micro and run a subset instead (see enabled_services)."
  type        = string
  default     = "t3.micro"
}

variable "enabled_services" {
  description = "Subset of ecs_services actually scheduled (desired_count = 1) — the rest stay defined but scaled to 0. Tried all 6 at once with tightened per-service memory (see ecs_services.memory): confirmed unstable — tasks OOM-kill and flap unpredictably because 6 JVMs' combined peak usage exceeds the t3.micro's ~916MB even with small heaps. Back to the stable subset of 3."
  type        = list(string)
  default     = ["auth-service", "database-server", "gateway"]
}

variable "ecs_services" {
  description = "The 6 self-built services, each getting an ECR repository, task definition, and ECS service. memory is the hard docker limit (MB) — sized per service so all 6 fit in the ~916MB the t3.micro registers with ECS; userinput-server is plain nginx (no JVM) so it gets much less."
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
      memory         = 170
    }
    database-server = {
      context_dir    = "DatabaseServer"
      container_port = 8081
      host_port      = 9081
      memory         = 230
    }
    notification-service = {
      context_dir    = "NotificationService"
      container_port = 8082
      host_port      = 9082
      memory         = 130
    }
    gateway = {
      context_dir    = "GatewayService"
      container_port = 8090
      host_port      = 9090
      memory         = 150
    }
    userinput-server = {
      context_dir    = "UserInputServer"
      container_port = 8080
      host_port      = 9080
      memory         = 65
    }
    reactive-service = {
      context_dir    = "ReactiveService"
      container_port = 8084
      host_port      = 9084
      memory         = 130
    }
  }
}
