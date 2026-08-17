package databaseserver.sql.controller;

import databaseserver.sql.service.RawSqlDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Raw SQL / Oracle-MSSQL-mentality demo endpoints — requires the {@code dev}
 * profile against real PostgreSQL (not the H2 profile).
 *
 * BASE URL: http://localhost:8081/api/rawsql
 *
 * GET  /api/rawsql/orders?status=PENDING                       → hand-written SQL JOIN
 * GET  /api/rawsql/explain?status=PENDING                      → real EXPLAIN ANALYZE execution plan
 * POST /api/rawsql/pessimistic-lock/demo?orderId=1&holdMillis=2000  → row-lock timeline
 * GET  /api/rawsql/revenue-by-city-procedure                   → calls fn_revenue_by_city()
 * POST /api/rawsql/execute        { "sql": "..." }              → runs arbitrary SQL, for the SQL console UI
 */
@RestController
@RequestMapping("/api/rawsql")
@RequiredArgsConstructor
public class RawSqlDemoController {

    private final RawSqlDemoService service;

    @GetMapping("/orders")
    public ResponseEntity<List<Map<String, Object>>> ordersRawSql(@RequestParam String status) {
        return ResponseEntity.ok(service.findOrdersRawSqlJoin(status));
    }

    @GetMapping("/explain")
    public ResponseEntity<List<Map<String, Object>>> explain(@RequestParam String status) {
        return ResponseEntity.ok(service.explainOrdersByStatus(status));
    }

    @PostMapping("/pessimistic-lock/demo")
    public ResponseEntity<List<String>> pessimisticLockDemo(
            @RequestParam Long orderId,
            @RequestParam(defaultValue = "2000") long holdMillis) {
        List<String> timeline = new CopyOnWriteArrayList<>();
        long startNanos = System.nanoTime();

        CompletableFuture<Void> threadA = CompletableFuture.runAsync(() ->
                service.lockAndHoldOrder(orderId, "Thread-A", holdMillis, startNanos, timeline));

        try {
            Thread.sleep(200); // give Thread-A time to acquire the lock before Thread-B tries
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        CompletableFuture<Void> threadB = CompletableFuture.runAsync(() ->
                service.lockAndHoldOrder(orderId, "Thread-B", holdMillis, startNanos, timeline));

        CompletableFuture.allOf(threadA, threadB).join();
        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/revenue-by-city-procedure")
    public ResponseEntity<Map<String, BigDecimal>> revenueByCityProcedure() {
        return ResponseEntity.ok(service.revenueByCityViaStoredProcedure());
    }

    // Runs whatever SQL the caller sends — local learning sandbox only, never expose this beyond permitAll dev use.
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> execute(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.executeRawSql(body.get("sql")));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
