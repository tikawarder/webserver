package databaseserver.sql.service;

import databaseserver.postgresql.model.Order;
import databaseserver.postgresql.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Hand-written native SQL, execution plans, pessimistic locking and a stored
 * function — the raw-SQL/Oracle-MSSQL mentality the JPA/Hibernate layer in
 * {@code databaseserver.postgresql} normally hides.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RawSqlDemoService {

    private final JdbcTemplate jdbcTemplate;
    private final OrderRepository orderRepository;

    // =========================================================================
    // NATIVE SQL JOIN — hand-written SQL, no JPQL
    // =========================================================================

    public List<Map<String, Object>> findOrdersRawSqlJoin(String status) {
        String sql = "SELECT o.id, o.product, o.amount, o.status, c.name AS customer_name " +
                "FROM demo_orders o JOIN demo_customers c ON o.customer_id = c.id " +
                "WHERE o.status = ?";
        log.info("[RAW SQL] {}", sql);
        return jdbcTemplate.queryForList(sql, status);
    }

    // =========================================================================
    // EXPLAIN ANALYZE — real PostgreSQL execution plan
    // =========================================================================

    // Oracle: EXPLAIN PLAN FOR ...; SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY). MSSQL: SSMS actual execution plan.
    public List<Map<String, Object>> explainOrdersByStatus(String status) {
        List<Map<String, Object>> plan = jdbcTemplate.queryForList("EXPLAIN ANALYZE SELECT * FROM demo_orders WHERE status = ?", status);
        log.info("[EXPLAIN ANALYZE] status={} plan={}", status, plan);
        return plan;
    }

    // =========================================================================
    // PESSIMISTIC LOCKING — two real transactions racing for the same row
    // =========================================================================

    @Transactional
    public void lockAndHoldOrder(Long orderId, String label, long holdMillis, long startNanos, List<String> timeline) {
        timeline.add(elapsedMs(startNanos) + "ms " + label + " requesting lock");
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        timeline.add(elapsedMs(startNanos) + "ms " + label + " acquired lock, holding " + holdMillis + "ms");
        try {
            Thread.sleep(holdMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        order.setProduct(label + "_touched");
        timeline.add(elapsedMs(startNanos) + "ms " + label + " committing (releasing lock)");
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    // =========================================================================
    // GENERIC EXECUTE — backs the SQL console UI, runs whatever SQL the user types
    // =========================================================================

    public Map<String, Object> executeRawSql(String sql) {
        String trimmed = sql.trim().toUpperCase();
        Map<String, Object> result = new LinkedHashMap<>();
        if (trimmed.startsWith("SELECT") || trimmed.startsWith("EXPLAIN") || trimmed.startsWith("WITH")) {
            result.put("rows", jdbcTemplate.queryForList(sql));
        } else {
            result.put("rowsAffected", jdbcTemplate.update(sql));
        }
        return result;
    }

    // =========================================================================
    // STORED FUNCTION (Postgres equivalent of a stored procedure that returns rows)
    // =========================================================================

    public Map<String, BigDecimal> revenueByCityViaStoredProcedure() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM fn_revenue_by_city()");
        log.info("[STORED FUNCTION] fn_revenue_by_city rows={}", rows.size());
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            result.put((String) row.get("city_name"), (BigDecimal) row.get("total_revenue"));
        }
        return result;
    }
}
