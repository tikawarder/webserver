# Demo Service — K8s Live Dashboard Plan

## What is this?

A new `demo-service` Spring Boot application running inside the cluster.
It visualizes Kubernetes behavior in real-time: pod scaling, HPA, CPU usage —
while simultaneously generating load on the other services.

## Goal

One browser window, two panels:
- **Left:** Load generator — start/stop/intensity control, request counter
- **Right:** Live K8s dashboard — pod list, HPA replica count, CPU%, live chart

## Architecture

```
browser
  ↓
demo-service (Spring Boot)
  ├── GET  /                     → serves the HTML/JS frontend
  ├── POST /load/start           → starts background load on database-server
  ├── POST /load/stop            → stops load
  └── GET  /metrics/stream       → SSE stream (pods, HPA, CPU every 2s)
        ↓
  Kubernetes Java Client         → reads K8s API (pods, HPA)
  RestTemplate                   → calls http://database-server/api/persons (load)
```

## K8s additions

```
k8s/
  demo-service/
    demo-service.yaml      # Deployment + ClusterIP Service (port 8099)
    rbac.yaml              # ServiceAccount + ClusterRole + ClusterRoleBinding
  ingress.yaml             # add /demo path → demo-service:8099
```

### RBAC — why it's needed

A pod inside the cluster cannot read K8s API by default.
A ServiceAccount with ClusterRole grants read access to pods and HPA objects.

```yaml
# ClusterRole permissions needed:
rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["get", "list"]
  - apiGroups: ["autoscaling"]
    resources: ["horizontalpodautoscalers"]
    verbs: ["get", "list"]
  - apiGroups: ["metrics.k8s.io"]
    resources: ["pods"]
    verbs: ["get", "list"]
```

## Spring Boot implementation

### Dependencies
```xml
<dependency>
    <groupId>io.fabric8</groupId>
    <artifactId>kubernetes-client</artifactId>
    <version>7.x</version>
</dependency>
```

### SSE endpoint (Server-Sent Events)
```java
@GetMapping(value = "/metrics/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ClusterSnapshot> stream() {
    return Flux.interval(Duration.ofSeconds(2))
               .map(tick -> collectSnapshot());
}
```

`ClusterSnapshot` contains:
- List of pods (name, status, ready, restarts)
- HPA: currentReplicas, desiredReplicas, currentCPU%, targetCPU%
- timestamp

### Load generator
```java
@PostMapping("/load/start")
public void startLoad(@RequestParam int requestsPerSecond) {
    // ScheduledExecutorService: fires N requests/sec to database-server
}
```

## Frontend (bundled HTML/JS)

Single `index.html` served by Spring Boot:
- **Left panel:** Start/Stop buttons, requests/sec slider, live counters (sent, 200, 500)
- **Right panel:**
  - Pod table: name | status | ready | restarts
  - HPA row: current replicas | CPU% | target
  - Chart.js line chart: CPU% over time (last 60 seconds)

## What this demonstrates

| K8s concept | How it shows |
|-------------|-------------|
| Auto-healing | Delete a pod → watch it reappear in the table |
| HPA scaling | Start heavy load → replica count rises live |
| Rolling update | `kubectl rollout restart` → pod status flickers, no 500s |
| Resource limits | CPU% approaches limit → HPA triggers |

## Implementation steps

1. Create `DemoServiceApplication` (Spring Boot, port 8099)
2. Add Fabric8 Kubernetes client, configure with in-cluster config
3. Implement `KubernetesMetricsService` — reads pods + HPA every 2s
4. Implement `LoadGeneratorService` — scheduled requests to database-server
5. Implement SSE controller (`/metrics/stream`)
6. Implement load control endpoints (`/load/start`, `/load/stop`)
7. Create `index.html` with Chart.js dashboard
8. Write K8s manifests: Deployment, Service, RBAC
9. Add `/demo` path to Ingress
10. Add to `deploy.sh`

## Access

After deploy: `http://webserver.local/demo`
