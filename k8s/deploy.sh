#!/bin/bash
set -e

echo "=== Switching to Minikube Docker ==="
eval $(minikube docker-env)

echo "=== Building Docker images ==="
docker build -t auth-service:latest ../AuthService
docker build -t database-server:latest ../DatabaseServer
docker build -t notification-service:latest ../NotificationService
docker build -t gateway-service:latest ../GatewayService
docker build -t userinput-server:latest ../UserInputServer

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

echo "=== Waiting for infrastructure pods to be ready ==="
kubectl wait --for=condition=ready pod -l app=postgres -n webserver --timeout=120s
kubectl wait --for=condition=ready pod -l app=kafka -n webserver --timeout=120s
kubectl wait --for=condition=ready pod -l app=redis -n webserver --timeout=60s
kubectl wait --for=condition=ready pod -l app=zipkin -n webserver --timeout=60s

echo "=== Done! ==="
echo "Frontend URL: $(minikube ip):30080"
