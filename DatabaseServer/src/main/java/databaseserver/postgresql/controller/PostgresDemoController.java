package databaseserver.postgresql.controller;

import databaseserver.postgresql.dto.CustomerDto;
import databaseserver.postgresql.dto.OrderDto;
import databaseserver.postgresql.service.PostgresDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller exposing all PostgreSQL demo endpoints.
 *
 * HOW TO USE:
 * Start the app (H2 mode) and call these endpoints with curl or Postman.
 * Watch the application logs to see the SQL queries being generated.
 *
 * BASE URL: http://localhost:8081/api/demo
 *
 * --- ACID ---
 * POST /api/demo/acid/transfer?fromId=1&toId=2&amount=100        → atomic success
 * POST /api/demo/acid/transfer-fail?fromId=1&toId=2&amount=10    → rollback demo
 *
 * --- JOIN ---
 * GET  /api/demo/join/inner     → only customers WITH orders
 * GET  /api/demo/join/left      → ALL customers (including 0 orders)
 *
 * --- N+1 ---
 * GET  /api/demo/n1/broken      → BAD: watch the log for multiple SELECTs
 * GET  /api/demo/n1/fixed       → GOOD: single SQL with JOIN FETCH
 *
 * --- AGGREGATES (INDEX demo) ---
 * GET  /api/demo/aggregates/revenue-per-city
 * GET  /api/demo/aggregates/order-count
 */
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class PostgresDemoController {

    private final PostgresDemoService service;

    // =========================================================================
    // ACID
    // =========================================================================

    /**
     * Demonstrates successful atomic transfer.
     * Both the debit and credit are committed together.
     */
    @PostMapping("/acid/transfer")
    public ResponseEntity<String> atomicTransfer(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam BigDecimal amount) {
        String result = service.atomicTransfer(fromId, toId, amount);
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates @Transactional rollback.
     * The debit write is reversed because an exception occurs before commit.
     * Always returns 500 — that is the expected behaviour!
     */
    @PostMapping("/acid/transfer-fail")
    public ResponseEntity<String> atomicTransferWithRollback(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam BigDecimal amount) {
        try {
            service.atomicTransferWithRollback(fromId, toId, amount);
            return ResponseEntity.ok("This line should never be reached.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(500).body(
                "ROLLBACK TRIGGERED: " + ex.getMessage() +
                "\nCheck DB: Order#" + fromId + " balance should be UNCHANGED."
            );
        }
    }

    // =========================================================================
    // JOIN
    // =========================================================================

    /**
     * INNER JOIN — only customers who have at least one order.
     * Charlie (0 orders) is excluded.
     */
    @GetMapping("/join/inner")
    public ResponseEntity<List<CustomerDto>> innerJoin() {
        return ResponseEntity.ok(service.getCustomersWithOrdersInnerJoin());
    }

    /**
     * LEFT JOIN — all customers, even those with zero orders.
     * Charlie appears with an empty orders list.
     */
    @GetMapping("/join/left")
    public ResponseEntity<List<CustomerDto>> leftJoin() {
        return ResponseEntity.ok(service.getAllCustomersLeftJoin());
    }

    // =========================================================================
    // N+1 PROBLEM
    // =========================================================================

    /**
     * BROKEN — N+1 problem in action.
     * Watch the log: you will see 1 SELECT for customers + 1 per customer for orders.
     */
    @GetMapping("/n1/broken")
    public ResponseEntity<List<CustomerDto>> n1Broken() {
        return ResponseEntity.ok(service.getCustomersN1Problem());
    }

    /**
     * FIXED — Single query with JOIN FETCH.
     * Watch the log: you will see exactly ONE SQL statement.
     */
    @GetMapping("/n1/fixed")
    public ResponseEntity<List<CustomerDto>> n1Fixed() {
        return ResponseEntity.ok(service.getCustomersFixed());
    }

    // =========================================================================
    // AGGREGATES (also benefits from indexes)
    // =========================================================================

    /**
     * Total revenue per city — demonstrates JOIN + GROUP BY + SUM.
     */
    @GetMapping("/aggregates/revenue-per-city")
    public ResponseEntity<Map<String, BigDecimal>> revenuePerCity() {
        return ResponseEntity.ok(service.revenuePerCity());
    }

    /**
     * Order count per customer — demonstrates JOIN + GROUP BY + COUNT.
     */
    @GetMapping("/aggregates/order-count")
    public ResponseEntity<Map<String, Long>> orderCount() {
        return ResponseEntity.ok(service.orderCountPerCustomer());
    }

    // =========================================================================
    // OPTIMISTIC LOCKING
    // =========================================================================

    /**
     * Demonstrates @Version-based Optimistic Locking conflict.
     *
     * Thread 1 updates the order in its own transaction → version increments in DB.
     * Thread 2 tries to save a stale copy (old version) → OptimisticLockException.
     * No DB lock is held at any point.
     *
     * POST /api/demo/optimistic-lock/demo?orderId=1
     */
    @PostMapping("/optimistic-lock/demo")
    public ResponseEntity<Map<String, Object>> optimisticLockDemo(@RequestParam Long orderId) {
        return ResponseEntity.ok(service.demonstrateOptimisticLock(orderId));
    }
}
