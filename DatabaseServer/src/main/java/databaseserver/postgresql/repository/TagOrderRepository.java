package databaseserver.postgresql.repository;

import databaseserver.postgresql.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TagOrderRepository extends JpaRepository<Order, Long> {

    /**
     * Fetch all orders WITH their tags in one query (JOIN FETCH on ManyToMany).
     * Without this: calling order.getTags() in a loop = N+1 problem again.
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.tags LEFT JOIN FETCH o.customer")
    List<Order> findAllWithTagsAndCustomer();

    /**
     * Fetch orders WITH their tags, filtered by tag name.
     * Shows: ManyToMany filtering via the join table.
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.tags t LEFT JOIN FETCH o.customer WHERE t.name = :tagName")
    List<Order> findByTagName(String tagName);

    /**
     * Revenue SUM grouped by tag name.
     * Demonstrates: ManyToMany JOIN + GROUP BY + aggregate.
     */
    @Query("SELECT t.name, SUM(o.amount) FROM Order o JOIN o.tags t GROUP BY t.name")
    List<Object[]> revenueByTag();
}
