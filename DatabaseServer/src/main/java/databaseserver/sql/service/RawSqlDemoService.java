package databaseserver.sql.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> row = jdbcTemplate.queryForMap("SELECT * FROM demo_orders WHERE id = ? FOR UPDATE", orderId);
        String originalProduct = (String) row.get("product");
        timeline.add(elapsedMs(startNanos) + "ms " + label + " acquired lock, row: product=" + originalProduct
                + " amount=" + row.get("amount") + ", holding " + holdMillis + "ms");
        try {
            Thread.sleep(holdMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int rowsAffected = jdbcTemplate.update("UPDATE demo_orders SET product = ? WHERE id = ?", label + "_touched", orderId);
        String updatedProduct = jdbcTemplate.queryForObject("SELECT product FROM demo_orders WHERE id = ?", String.class, orderId);
        timeline.add(elapsedMs(startNanos) + "ms " + label + " update affected " + rowsAffected + " row(s), row is now product=" + updatedProduct);

        // restore the original value so repeated demo runs don't permanently rename the row
        jdbcTemplate.update("UPDATE demo_orders SET product = ? WHERE id = ?", originalProduct, orderId);
        timeline.add(elapsedMs(startNanos) + "ms " + label + " reverted product to '" + originalProduct + "', committing (releasing lock)");
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    // =========================================================================
    // GENERIC EXECUTE — backs the SQL console UI, runs whatever SQL the user types
    // =========================================================================

    // Statement.execute() reports whether the DB produced a ResultSet or an update count,
    // instead of us guessing from the SQL text (which breaks on lowercase, leading whitespace, CTEs...).
    public Map<String, Object> executeRawSql(String sql) {
        return jdbcTemplate.execute((Connection connection) -> {
            Map<String, Object> result = new LinkedHashMap<>();
            try (Statement statement = connection.createStatement()) {
                boolean isResultSet = statement.execute(sql);
                if (isResultSet) {
                    try (ResultSet resultSet = statement.getResultSet()) {
                        result.put("rows", extractRows(resultSet));
                    }
                } else {
                    result.put("rowsAffected", statement.getUpdateCount());
                }
            }
            return result;
        });
    }

    private List<Map<String, Object>> extractRows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            rows.add(row);
        }
        return rows;
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
