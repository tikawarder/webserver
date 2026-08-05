# --- ECS: run pre-built images, never build ---

# Required once per account before ECS capacity providers work — AWS normally
# creates this automatically the first time you use ECS via the console, but
# API-only usage (Terraform) needs it created explicitly.
resource "aws_iam_service_linked_role" "ecs" {
  aws_service_name = "ecs.amazonaws.com"
}

resource "aws_ecs_cluster" "main" {
  name = "webserver-learning-cluster"
}

# --- Container instance (a plain EC2 that registers itself into the cluster) ---

data "aws_ssm_parameter" "ecs_ami" {
  name = "/aws/service/ecs/optimized-ami/amazon-linux-2023/recommended/image_id"
}

resource "aws_iam_role" "ecs_instance" {
  name = "webserver-learning-ecs-instance-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_instance" {
  role       = aws_iam_role.ecs_instance.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_instance" {
  name = "webserver-learning-ecs-instance-profile"
  role = aws_iam_role.ecs_instance.name
}

resource "aws_launch_template" "ecs" {
  name_prefix   = "webserver-learning-ecs-"
  image_id      = data.aws_ssm_parameter.ecs_ami.value
  instance_type = var.app_instance_type

  iam_instance_profile {
    name = aws_iam_instance_profile.ecs_instance.name
  }

  vpc_security_group_ids = [aws_security_group.app.id]

  # Tells the ECS agent which cluster to join — this one line is what turns a
  # plain EC2 instance into an ECS "container instance".
  user_data = base64encode(<<-EOT
    #!/bin/bash
    echo ECS_CLUSTER=${aws_ecs_cluster.main.name} >> /etc/ecs/ecs.config
  EOT
  )

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name = "webserver-learning-app-instance"
    }
  }
}

resource "aws_autoscaling_group" "ecs" {
  name                = "webserver-learning-ecs-asg"
  vpc_zone_identifier = [aws_subnet.public.id]
  min_size            = 1
  max_size            = 1
  desired_capacity    = 1

  launch_template {
    id      = aws_launch_template.ecs.id
    version = "$Latest"
  }

  # Required by the ECS capacity provider to recognize instances from this ASG.
  tag {
    key                 = "AmazonECSManaged"
    value               = true
    propagate_at_launch = true
  }
}

resource "aws_ecs_capacity_provider" "main" {
  name = "webserver-learning-capacity-provider"

  auto_scaling_group_provider {
    auto_scaling_group_arn = aws_autoscaling_group.ecs.arn

    managed_scaling {
      status          = "ENABLED"
      target_capacity = 100
    }
  }

  depends_on = [aws_iam_service_linked_role.ecs]
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.name
  capacity_providers = [aws_ecs_capacity_provider.main.name]

  default_capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.main.name
    weight            = 1
  }
}

# --- Task execution role: lets ECS itself pull from ECR and ship logs ---
# (Distinct from a "task role", which would give the running app AWS API
# access — none of these services call AWS APIs, so no task role is needed.)

resource "aws_iam_role" "ecs_task_execution" {
  name = "webserver-learning-ecs-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/webserver-learning"
  retention_in_days = 3
}

# --- Task definitions + services, one pair per self-built service ---
#
# Infra hostnames (postgres_db, keycloak, kafka, zipkin, redis) from
# docker-compose.yml don't resolve here — different EC2 instance, different
# network — so every URL below points at the infra server's IP instead.
# Keycloak and the app's own public URL use the infra server's PUBLIC IP,
# because that's the host a browser actually sees (and what ends up baked
# into the JWT's "iss" claim) — the private IP would fail JWT issuer checks.

locals {
  infra_private = aws_instance.infra_server.private_ip
  infra_public  = aws_eip.infra_server.public_ip

  service_env = {
    auth-service = {
      SPRING_DATASOURCE_URL         = "jdbc:postgresql://${local.infra_private}:5432/authdb"
      SPRING_DATASOURCE_USERNAME    = "user"
      SPRING_DATASOURCE_PASSWORD    = "password"
      SPRING_JPA_HIBERNATE_DDL_AUTO = "update"
      ZIPKIN_ENDPOINT               = "http://${local.infra_private}:9411/api/v2/spans"
      JAVA_TOOL_OPTIONS             = "-Xmx80m"
    }
    database-server = {
      SPRING_DATASOURCE_URL                                 = "jdbc:postgresql://${local.infra_private}:5432/usersdb"
      SPRING_DATASOURCE_USERNAME                            = "user"
      SPRING_DATASOURCE_PASSWORD                            = "password"
      SPRING_JPA_HIBERNATE_DDL_AUTO                         = "update"
      SPRING_PROFILES_ACTIVE                                = "dev"
      KAFKA_BOOTSTRAP_SERVERS                               = "${local.infra_private}:9092"
      ZIPKIN_ENDPOINT                                       = "http://${local.infra_private}:9411/api/v2/spans"
      SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI = "http://${local.infra_public}:8180/realms/webserver-realm/protocol/openid-connect/certs"
      SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI  = "http://${local.infra_public}:8180/realms/webserver-realm"
      REDIS_HOST                                            = local.infra_private
      JAVA_TOOL_OPTIONS                                     = "-Xmx100m"
    }
    notification-service = {
      KAFKA_BOOTSTRAP_SERVERS = "${local.infra_private}:9092"
      ZIPKIN_ENDPOINT         = "http://${local.infra_private}:9411/api/v2/spans"
      JAVA_TOOL_OPTIONS       = "-Xmx60m"
    }
    gateway = {
      ZIPKIN_ENDPOINT                                       = "http://${local.infra_private}:9411/api/v2/spans"
      SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI = "http://${local.infra_public}:8180/realms/webserver-realm/protocol/openid-connect/certs"
      SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI  = "http://${local.infra_public}:8180/realms/webserver-realm"
      JAVA_TOOL_OPTIONS                                     = "-Xmx70m"
    }
    userinput-server = {
      REACT_APP_API_URL = "http://${local.infra_public}:9090"
    }
    reactive-service = {
      SPRING_R2DBC_URL      = "r2dbc:postgresql://${local.infra_private}:5432/reactivedb"
      SPRING_R2DBC_USERNAME = "user"
      SPRING_R2DBC_PASSWORD = "password"
      SPRING_SQL_INIT_MODE  = "always"
      JAVA_TOOL_OPTIONS     = "-Xmx60m"
    }
  }
}

resource "aws_ecs_task_definition" "services" {
  for_each = var.ecs_services

  family                   = "webserver-learning-${each.key}"
  requires_compatibilities = ["EC2"]
  network_mode             = "bridge"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  memory                   = each.value.memory
  cpu                      = 256

  container_definitions = jsonencode([{
    name      = each.key
    image     = "${aws_ecr_repository.services[each.key].repository_url}:latest"
    essential = true
    portMappings = [{
      containerPort = each.value.container_port
      hostPort      = each.value.host_port
      protocol      = "tcp"
    }]
    environment = [
      for k, v in local.service_env[each.key] : { name = k, value = v }
    ]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = each.key
      }
    }
  }])
}

resource "aws_ecs_service" "services" {
  for_each = var.ecs_services

  name            = each.key
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.services[each.key].arn
  desired_count   = contains(var.enabled_services, each.key) ? 1 : 0
  launch_type     = "EC2"

  depends_on = [aws_ecs_cluster_capacity_providers.main]
}
