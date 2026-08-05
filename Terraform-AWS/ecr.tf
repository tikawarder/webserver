# One repository per self-built service — force_delete so `terraform destroy`
# can remove the repository even with pushed images still inside it.
resource "aws_ecr_repository" "services" {
  for_each = var.ecs_services

  name                 = "webserver-learning/${each.key}"
  image_tag_mutability = "MUTABLE"
  force_delete         = true
}
