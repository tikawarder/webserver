# JMeter load test — ReactiveService `/hello`

Simple load test against the unauthenticated `GET /hello` endpoint on `reactive-service`.

## Run it

```bash
docker compose up -d reactive-service   # starts postgres_db + reactive-service
jmeter -n -t jmeter/reactive-hello-load-test.jmx -l jmeter/results.jtl
```

## Test plan

- Thread Group: 20 concurrent users, 10s ramp-up, 10 loops each (200 requests total)
- Sampler: `GET http://localhost:9084/hello`
- Assertions: response code 200, response time < 500ms

## Sample result

```
summary = 200 in 00:00:10 = 20.9/s Avg: 3 Min: 1 Max: 38 Err: 0 (0.00%)
```
