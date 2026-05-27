package databaseserver.postgresql.repository;

import databaseserver.postgresql.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Order queries.
 *
 * Demonstrates:
 * - JOIN queries from the order side (fetch customer data with order)
 * - Aggregate queries (SUM, AVG, COUNT)
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // =========================================================================
    // JOIN DEMO — fetch orders with their customer in one query
    // =========================================================================

    /**
     * INNER JOIN from Order side.
     * Fetches every order together with its customer data in a single query.
     * Only returns orders that HAVE a customer (always true with NOT NULL FK,
     * but the SQL concept is important).
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer")
    List<Order> findAllWithCustomer();

    /**
     * Filtered JOIN: orders by status, with customer data.
     * Demonstrates: JOIN + WHERE combined.
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :status")
    List<Order> findByStatusWithCustomer(@Param("status") Order.OrderStatus status);

    // =========================================================================
    // AGGREGATE QUERIES
    // =========================================================================

    /**
     * Total revenue per city.
     * Demonstrates: JOIN + GROUP BY + aggregate function (SUM).
     *
     * Equivalent SQL:
     *   SELECT c.city, SUM(o.amount)
     *   FROM demo_orders o
     *   INNER JOIN demo_customers c ON o.customer_id = c.id
     *   GROUP BY c.city
     */
    @Query("SELECT c.city, SUM(o.amount) FROM Order o JOIN o.customer c GROUP BY c.city")
    List<Object[]> totalRevenuePerCity();

    /**
     * Count orders per customer.
     * Demonstrates: GROUP BY + COUNT.
     */
    @Query("SELECT c.name, COUNT(o) FROM Order o JOIN o.customer c GROUP BY c.name")
    List<Object[]> orderCountPerCustomer();

    /**
     * Average order amount for a specific customer.
     */
    @Query("SELECT AVG(o.amount) FROM Order o WHERE o.customer.id = :customerId")
    BigDecimal averageAmountByCustomer(@Param("customerId") Long customerId);
}
