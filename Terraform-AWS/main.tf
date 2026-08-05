# 1. Provider setup
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# --- 2. NETWORK ---
# Unlike GCP's "default network", AWS has no default VPC to fall back on —
# the VPC, subnet, and Internet Gateway all have to be built explicitly.

resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "webserver-learning-vpc"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
}

# Single public subnet — no NAT Gateway (never free), so for this learning
# setup the instance sits directly in the public subnet instead.
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true

  tags = {
    Name = "webserver-learning-public-subnet"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# --- 3. FIREWALL (Security Group) ---
# No port 22 open: administration goes through SSM Session Manager instead,
# which needs no inbound port at all (the AWS counterpart to GCP's IAP
# pattern, minus even having to open a port for it).
resource "aws_security_group" "app" {
  name_prefix = "webserver-learning-"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "React app / UI"
    from_port   = var.app_port
    to_port     = var.app_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Phase 2: the ECS-hosted app services (different EC2 instance) need to reach
  # Postgres/Keycloak/Kafka/Zipkin/Redis here, and vice versa — both instances
  # share this security group, so this just opens traffic within the VPC.
  ingress {
    description = "Internal VPC traffic between infra and app instances"
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.main.cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# --- 4. IAM: SSM access via role ---
# No access key on the instance — a role is attached instead, which the EC2
# metadata service exchanges for temporary credentials automatically.
resource "aws_iam_role" "ssm" {
  name = "webserver-learning-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm" {
  role       = aws_iam_role.ssm.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ssm" {
  name = "webserver-learning-ssm-profile"
  role = aws_iam_role.ssm.name
}

# --- 5. THE SERVER (EC2) ---
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

resource "aws_instance" "infra_server" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.app.id]
  iam_instance_profile   = aws_iam_instance_profile.ssm.name

  # The AMI's default root volume (~8GiB) filled up mid-boot pulling all 8
  # infra images, which silently killed the rest of user_data (including the
  # docker-compose up call) — 30GiB stays within the Free Tier EBS allowance.
  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  # Installs Docker + Compose and clones the repo on first boot — the same
  # pattern as the GCP VM's metadata_startup_script. Phase 2: only starts the
  # 8 infra services (ready-made images, no build step) — the 6 self-built
  # services now run on the separate ECS container instance instead.
  user_data = <<-EOT
    #!/bin/bash
    dnf install -y docker git
    systemctl enable --now docker
    curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
      -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose

    cd /home/ec2-user
    git clone --branch ${var.repo_branch} ${var.repo_url} webserver || true

    cd /home/ec2-user/webserver
    # AI/.env is gitignored (holds local secrets) — docker-compose.yml requires
    # it to exist as an env_file, so a placeholder unblocks the AI-less services.
    mkdir -p AI
    echo "GEMINI_API_KEY=not-configured" > AI/.env

    /usr/local/bin/docker-compose up -d postgres_db keycloak zookeeper kafka zipkin redis prometheus grafana
  EOT

  tags = {
    Name = "webserver-learning-infra-server"
  }
}

resource "aws_eip" "infra_server" {
  instance = aws_instance.infra_server.id
  domain   = "vpc"
}
