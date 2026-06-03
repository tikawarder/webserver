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
  postgres/               # database + PersistentVolumeClaim (disk storage)
  kafka/                  # Zookeeper + Kafka
  auth-service/           # Spring Boot — authentication
  database-server/        # Spring Boot — persons CRUD
  notification-service/   # Spring Boot — Kafka consumer
  gateway/                # Spring Cloud Gateway — routing
  userinput-server/       # React/Nginx frontend (NodePort: accessible from outside)
```

## Docker Compose vs Kubernetes

| | Docker Compose | Kubernetes |
|--|----------------|------------|
| Start | `docker-compose up` | `kubectl apply -f k8s/` |
| Container crash | does not restart | automatically restarts |
| Scaling | manual, cumbersome | `kubectl scale --replicas=3` |
| Service discovery | container name | Service name |
| Passwords | `.env` file | Secret |
| Configuration | environment: block | ConfigMap |
| Disk storage | volumes: block | PersistentVolumeClaim |

## Local testing (Minikube)

```bash
# 1. Start Minikube
minikube start

# 2. Point Docker to Minikube's internal daemon (required in every new terminal!)
eval $(minikube docker-env)

# 3. Build images and deploy
cd k8s
./deploy.sh

# 4. Open frontend
minikube ip   # → http://<ip>:30080
```

## Useful commands

```bash
# Check pod status
kubectl get pods -n webserver

# Scale to 3 parallel database-server instances
kubectl scale deployment database-server --replicas=3 -n webserver

# View logs
kubectl logs -n webserver deployment/gateway

# Restart a pod (e.g. after a new image build)
kubectl rollout restart deployment/gateway -n webserver

# Delete everything (pods, services, configmap, secrets, PVC)
kubectl delete namespace webserver

# Stop Minikube (preserves state, releases all memory)
minikube stop

# Start Minikube again (resumes from where it left off)
minikube start

# Fully delete the cluster
minikube delete

# Check if Minikube is running (empty = stopped, no memory used)
docker ps | grep minikube
```

## Key learnings

- **Service name** is the hostname in K8s (not the container name like in Docker Compose)
- `imagePullPolicy: Never` is required for locally built images (Minikube)
- **NodePort** service type is needed for external access (userinput-server: 30080)
- All other services use **ClusterIP** — only visible inside the cluster
- `eval $(minikube docker-env)` only applies to the current terminal session
