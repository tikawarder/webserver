package databaseserver.postgresql.repository;

import databaseserver.postgresql.model.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Customer queries.
 *
 * Contains both the BROKEN (N+1) and FIXED (JOIN FETCH) versions
 * side by side so you can compare them directly.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // =========================================================================
    // N+1 PROBLEM DEMO
    // =========================================================================

    /**
     * BAD: This query loads all customers, but does NOT fetch orders.
     * When you call customer.getOrders() on each one, Hibernate fires
     * a NEW query per customer → N+1 queries total.
     *
     * findAll() from JpaRepository also does the same thing.
     * Use findAllWithN1Problem() to make the intent explicit in tests.
     */
    @Query("SELECT c FROM Customer c")
    List<Customer> findAllWithN1Problem();

    // =========================================================================
    // N+1 SOLUTION 1: JOIN FETCH
    // =========================================================================

    /**
     * GOOD: JOIN FETCH tells Hibernate to load customers AND their orders
     * in a SINGLE SQL query using a JOIN.
     *
     * Generated SQL (approx):
     *   SELECT c.*, o.* FROM demo_customers c
     *   LEFT JOIN demo_orders o ON o.customer_id = c.id
     */
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.orders")
    List<Customer> findAllWithOrdersJoinFetch();

    // =========================================================================
    // N+1 SOLUTION 2: @EntityGraph
    // =========================================================================

    /**
     * GOOD: @EntityGraph is the annotation-based alternative to JOIN FETCH.
     * Spring Data generates the JOIN automatically.
     * Useful when you don't want to write custom JPQL.
     */
    @EntityGraph(attributePaths = {"orders"})
    @Query("SELECT c FROM Customer c")
    List<Customer> findAllWithOrdersEntityGraph();

    // =========================================================================
    // JOIN DEMO — JPQL equivalents
    // =========================================================================

    /**
     * INNER JOIN: returns only customers who HAVE at least one order.
     * Customers with zero orders are excluded.
     */
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.orders o")
    List<Customer> findCustomersWithAtLeastOneOrder();

    /**
     * LEFT JOIN: returns ALL customers, even those with no orders.
     * For customers without orders, the orders collection will be empty.
     */
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN c.orders o")
    List<Customer> findAllCustomersLeftJoin();

    // =========================================================================
    // INDEX DEMO — queries that USE the email/city index
    // =========================================================================

    /**
     * This query benefits from idx_customer_email index.
     * In a real PostgreSQL EXPLAIN ANALYZE, you would see "Index Scan" here.
     */
    Optional<Customer> findByEmail(String email);

    /**
     * This query benefits from idx_customer_city index.
     */
    List<Customer> findByCity(String city);

    // =========================================================================
    // @OneToOne JOIN FETCH queries
    // =========================================================================

    /**
     * Load ALL customers with their profiles in ONE query.
     * LEFT JOIN FETCH: Charlie (no profile) still appears with profile=null.
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.profile")
    List<Customer> findAllWithProfile();

    /**
     * Load a single customer with profile AND orders in as few queries as possible.
     */
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.profile WHERE c.id = :id")
    Optional<Customer> findByIdWithProfileAndOrders(@org.springframework.data.repository.query.Param("id") Long id);
}
