#!/bin/bash
set -e

echo "=== Switching to Minikube Docker ==="
eval $(minikube docker-env)

echo "=== Building Docker images ==="
GIT_SHA=$(git -C .. rev-parse --short HEAD)
docker build --no-cache -t auth-service:$GIT_SHA -t auth-service:latest ../AuthService
docker build --no-cache -t database-server:$GIT_SHA -t database-server:latest ../DatabaseServer
docker build --no-cache -t notification-service:$GIT_SHA -t notification-service:latest ../NotificationService
docker build --no-cache -t gateway-service:$GIT_SHA -t gateway-service:latest ../GatewayService
docker build --no-cache -t userinput-server:$GIT_SHA -t userinput-server:latest ../UserInputServer
echo "Built images with tag: $GIT_SHA"

echo "=== Applying Kubernetes manifests ==="
kubectl apply -f namespace.yaml
kubectl apply -f secrets.yaml
kubectl apply -f configmap.yaml
kubectl apply -f postgres/
kubectl apply -f redis/
kubectl apply -f zipkin/
kubectl apply -f kafka/
kubectl apply -f auth-service/
kubectl apply -f database-server/
kubectl apply -f notification-service/
kubectl apply -f gateway/
kubectl apply -f userinput-server/
kubectl apply -f ingress.yaml

echo "=== Waiting for infrastructure pods to be ready ==="
kubectl wait --for=condition=ready pod -l app=postgres -n webserver --timeout=120s
kubectl wait --for=condition=ready pod -l app=kafka -n webserver --timeout=120s
kubectl wait --for=condition=ready pod -l app=redis -n webserver --timeout=60s
kubectl wait --for=condition=ready pod -l app=zipkin -n webserver --timeout=60s

echo "=== Done! ==="
echo "Add this to /etc/hosts if not already there:"
echo "  $(minikube ip)  webserver.local"
echo ""
echo "Frontend URL: http://webserver.local"
echo "API URL:      http://webserver.local/api/"
