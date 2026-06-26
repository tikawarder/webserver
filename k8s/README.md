# Kubernetes Deployment

## What is this?

The full webserver project running in Kubernetes on a local Minikube cluster.
This is the next level after Docker Compose: automatic restarts, scaling, and service discovery.

## Structure

```
k8s/
  namespace.yaml          # "webserver" namespace — logical separator
  secrets.yaml            # passwords (base64 encoded, never commit in production)
  configmap.yaml          # URLs, ports, non-sensitive config
  ingress.yaml            # Nginx Ingress — path-based routing (webserver.local)
  postgres/               # database + PersistentVolumeClaim (disk storage)
  kafka/                  # Zookeeper + Kafka
  redis/                  # Redis cache
  zipkin/                 # Distributed tracing
  auth-service/           # Spring Boot — authentication
  database-server/        # Spring Boot — persons CRUD + hpa.yaml (auto-scaling)
  notification-service/   # Spring Boot — Kafka consumer
  gateway/                # Spring Cloud Gateway — routing + global CORS
  userinput-server/       # React/Nginx frontend
  DEMO_SERVICE_PLAN.md    # Planned: live K8s dashboard + load generator
```

## Docker Compose vs Kubernetes

| | Docker Compose | Kubernetes |
|--|----------------|------------|
| Start | `docker-compose up` | `kubectl apply -f k8s/` |
| Container crash | does not restart | automatically restarts |
| Scaling | manual, cumbersome | `kubectl scale --replicas=3` |
| Auto-scaling | not supported | HorizontalPodAutoscaler |
| Service discovery | container name | Service name |
| Passwords | `.env` file | Secret |
| Configuration | environment: block | ConfigMap |
| Disk storage | volumes: block | PersistentVolumeClaim |
| External routing | port mapping | Ingress (domain + path) |
| Health checks | healthcheck: block | liveness + readiness probes |
| Resource limits | not enforced | requests + limits per pod |

## What was implemented

- **Liveness + Readiness probes** — Spring Actuator `/actuator/health/liveness` and `/actuator/health/readiness`
- **Resource requests + limits** — 256Mi/200m request, 512Mi/500m limit per Spring Boot service
- **Rolling update strategy** — `maxSurge: 1`, `maxUnavailable: 0` (zero-downtime deploy)
- **Ingress** — Nginx controller, `http://webserver.local` replaces NodePort
- **Gateway-level CORS** — `globalcors` in Spring Cloud Gateway, no `@CrossOrigin` in services
- **HPA** — database-server auto-scales 1→3 pods at 50% CPU threshold

## Local testing (Minikube)

```bash
# 1. Start Minikube
minikube start
minikube addons enable ingress
minikube addons enable metrics-server

# 2. Add to /etc/hosts (once)
echo "$(minikube ip)  webserver.local" | sudo tee -a /etc/hosts

# 3. Build and deploy
cd k8s
./deploy.sh

# 4. Open frontend
# http://webserver.local
# Login: admin / password
```

## Live demo commands

```bash
# Watch pods in real-time
kubectl get pods -n webserver -w

# Watch HPA scaling
kubectl get hpa -n webserver -w

# Trigger load (generates CPU spike → HPA scales up)
for i in $(seq 1 500); do curl -s http://webserver.local/api/persons?page=0 -b /tmp/cookies.txt > /dev/null & done

# Kill a pod (auto-healing demo)
kubectl delete pod -n webserver -l app=database-server

# Rolling update demo (watch with curl loop in parallel)
while true; do curl -s -o /dev/null -w "%{http_code}\n" http://webserver.local/api/persons?page=0 -b /tmp/cookies.txt; sleep 0.5; done
kubectl rollout restart deployment/database-server -n webserver
```

## Useful commands

```bash
# Check pod status
kubectl get pods -n webserver

# Scale manually
kubectl scale deployment database-server --replicas=3 -n webserver

# View logs
kubectl logs -n webserver deployment/gateway

# Restart a pod (picks up new image)
kubectl rollout restart deployment/gateway -n webserver

# Delete everything
kubectl delete namespace webserver

# Stop/start Minikube
minikube stop
minikube start
minikube delete

# Check if Minikube is running
docker ps | grep minikube
```

## Key learnings

- **Service name** is the hostname in K8s (not the container name like in Docker Compose)
- `imagePullPolicy: Never` is required for locally built images (Minikube)
- **Ingress** replaces NodePort for external access — requires `minikube addons enable ingress`
- **Liveness vs Readiness:** liveness restarts the pod, readiness removes it from load balancer
- **HPA requires** resource requests to be set — without them it cannot calculate utilization
- **Rolling update** only achieves zero-downtime with `replicas >= 2` and `maxUnavailable: 0`
- **Gateway handles CORS** — internal services should not check CORS (`cors.disable()`)
- `eval $(minikube docker-env)` only applies to the current terminal session
- `deploy.sh` uses `--no-cache` + git SHA tag to prevent stale Docker layers
