# Skill: Convert docker-compose.yml to Kubernetes Manifests

## Usage
`/k8s-convert`

## Steps

1. **Read the source file**
   - Load `/home/me/Documents/cv/webserver/docker-compose.yml`
   - Parse all services, ports, environment variables, volumes, networks, and health checks

2. **Generate Kubernetes manifests for each service**

   For every service in docker-compose, create:
   - `Deployment.yaml` — container spec, replicas, resource limits, liveness/readiness probes
   - `Service.yaml` — ClusterIP for internal, or NodePort/LoadBalancer for externally accessed services
   - `ConfigMap.yaml` — non-sensitive environment variables
   - `Secret.yaml` — sensitive values (passwords, keys) as base64-encoded data

3. **Generate Ingress**
   - Create `Ingress.yaml` to route external traffic (replacing exposed ports)
   - Optimized for k3d with Traefik ingress controller

4. **Output structure**
   ```
   /home/me/Documents/cv/webserver/k8s/
   ├── namespace.yaml
   ├── database-server/
   │   ├── deployment.yaml
   │   ├── service.yaml
   │   └── configmap.yaml
   ├── userinput-server/
   │   ├── deployment.yaml
   │   └── service.yaml
   ├── postgres/
   │   ├── deployment.yaml
   │   ├── service.yaml
   │   ├── pvc.yaml
   │   └── secret.yaml
   ├── kafka/
   │   ├── deployment.yaml
   │   └── service.yaml
   └── ingress.yaml
   ```

5. **Learning summary**
   - Explain docker-compose vs K8s concept mapping
   - Point out what each resource type does and when it is used
   - Note: how to apply with `kubectl apply -f k8s/`
