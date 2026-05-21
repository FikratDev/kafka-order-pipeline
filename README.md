# Kafka Order Pipeline

An event-driven order processing pipeline built with Spring Boot, Apache Kafka, and PostgreSQL.

## What it does

A REST API accepts order requests and immediately returns `202 Accepted`. The order is published as a JSON event to a Kafka topic. A consumer reads from that topic, deduplicates, and persists the order to PostgreSQL. The producer and consumer run in the same application — in a real system they would be separate services.

```
POST /api/orders
       │
       ▼
  OrderProducer ──► Kafka topic: order.placed
                                      │
                                      ▼
                               OrderConsumer ──► PostgreSQL
```

## Stack

| Layer | Tech |
|---|---|
| API | Java 21, Spring Boot 3.2 |
| Messaging | Apache Kafka (KRaft, no Zookeeper) |
| Database | PostgreSQL 16 |
| Docs | Swagger UI (`/swagger-ui.html`) |
| Infra | Docker Compose |

## Quick start

```bash
docker compose up --build
```

API at `http://localhost:8080` · Swagger at `http://localhost:8080/swagger-ui.html`

## API

### Place an order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-42",
    "items": ["item-A", "item-B"],
    "totalAmount": 59.99
  }'
```
```json
{
  "orderId": "3f2a1b4c-...",
  "status": "ACCEPTED"
}
```

The order is processed asynchronously. Check back in a moment:

### List processed orders
```bash
curl http://localhost:8080/api/orders
```

### Get a single order
```bash
curl http://localhost:8080/api/orders/3f2a1b4c-...
```

## Design notes

**Decoupled by design** — the HTTP handler returns as soon as the event is published. The consumer processes independently, which means the API stays fast even if the DB is slow.

**Idempotent consumer** — before saving, the consumer checks if the order ID already exists. Kafka guarantees at-least-once delivery; this check prevents duplicate rows on retry.

**KRaft mode** — Kafka runs without Zookeeper (KRaft, available since Kafka 3.x). The Docker Compose setup is a single-broker cluster, appropriate for local development.

**3 partitions** — the `order.placed` topic is created with 3 partitions. Orders are keyed by `orderId`, so all events for a given order land on the same partition in order.
