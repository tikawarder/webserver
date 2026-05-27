package databaseserver.postgresql.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the PostgreSQL demo module.
 *
 * Relationship: Customer (1) --- (*) Order
 *
 * Used to demonstrate:
 * - JOIN queries (INNER, LEFT)
 * - N+1 problem and its solution with JOIN FETCH
 * - Index on email field
 */
@Entity
@Table(
    name = "demo_customers",
    indexes = {
        // --- INDEX DEMO ---
        // This index speeds up lookups by email (used in WHERE clauses).
        // Without it: full table scan (Seq Scan).
        // With it: index scan — much faster on large tables.
        @Index(name = "idx_customer_email", columnList = "email"),
        @Index(name = "idx_customer_city", columnList = "city")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String city;

    /**
     * LAZY loading = N+1 problem source.
     * When we call customer.getOrders() in a loop AFTER the initial query,
     * Hibernate fires a NEW SQL query for every single customer.
     *
     * Solution: use JOIN FETCH in the repository query, or @EntityGraph.
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
}
