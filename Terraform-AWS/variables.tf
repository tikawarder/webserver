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
