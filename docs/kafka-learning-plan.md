# Kafka Learning Plan — Event-driven-arch branch

## Current state

This branch contains the first working Kafka implementation: a producer and consumer
living in the **same DatabaseServer** service. When a person is saved, a `UserCreatedEvent`
is sent to the `user-created` topic and immediately consumed by the same application.

This is intentionally simple — a "hello world" for Kafka — before extracting the consumer
into a separate service (done in the `microservices` branch).

---

## Learning steps planned

### Step 1 — Kafka UI (Kafdrop)
Add [Kafdrop](https://github.com/obsidiandynamics/kafdrop) to `docker-compose.yml` so the
topics, messages, partitions and consumer lag are visible in a browser.

**What you learn:** what Kafka actually looks like from the outside — topics, offsets,
consumer groups. Essential before going deeper.

```yaml
# docker-compose.yml addition
kafdrop:
  image: obsidiandynamics/kafdrop
  ports:
    - "9000:9000"
  environment:
    KAFKA_BROKERCONNECT: kafka:29092
```

URL after start: http://localhost:9000

---

### Step 2 — Dead Letter Queue (DLQ)
Make the consumer deliberately fail for certain events, and route failed messages to a
`user-created.DLT` topic instead of blocking forever.

**What you learn:** Kafka is at-least-once — if a consumer throws, the message is
retried. Without a DLQ, one bad message can block the entire partition.

Spring Kafka annotation to add: `@RetryableTopic`

---

### Step 3 — Manual offset management
Switch from auto-commit to manual acknowledgement (`AckMode.MANUAL_IMMEDIATE`).

**What you learn:** the difference between "message received" and "message processed".
Auto-commit can cause message loss on crash; manual ack gives explicit control.

---

### Step 4 — Partitions and ordering
Add a second partition to the `user-created` topic and observe how message ordering
behaves with multiple consumers in the same group.

**What you learn:** Kafka guarantees order **within a partition**, not across partitions.
The producer key (`event.getId()`) controls which partition receives the message.

---

## Merge plan

After completing the steps above, merge this branch into `master` as a self-contained
Kafka learning milestone. The `microservices` branch already shows the next evolution:
producer and consumer in separate services with the Transactional Outbox Pattern.
