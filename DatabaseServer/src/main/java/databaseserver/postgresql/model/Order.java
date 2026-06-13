package databaseserver.postgresql.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an order placed by a Customer.
 *
 * Relationships:
 *   Order (*) --- (1) Customer    @ManyToOne  (owning side — has the FK)
 *   Order (*) --- (*) Tag         @ManyToMany (owning side — has the join table)
 */
@Entity
@Table(
    name = "demo_orders",
    indexes = {
        // Index on customer_id — the foreign key column.
        // Without this index, every JOIN or WHERE customer_id = ? causes a full table scan.
        @Index(name = "idx_order_customer_id", columnList = "customer_id"),
        @Index(name = "idx_order_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Version — enables Optimistic Locking.
     * Hibernate automatically increments this value on every UPDATE.
     * If two transactions load the same entity and both try to save it,
     * the second one fails with OptimisticLockException (stale version detected).
     * No DB-level lock is held — concurrency is resolved at commit time.
     */
    @Version
    private Long version;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * @ManyToOne — owning side of Customer <-> Order.
     * The FK column 'customer_id' lives in THIS table (demo_orders).
     * FetchType.LAZY: customer data is NOT loaded with the order automatically.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * @ManyToMany — owning side of Order <-> Tag.
     *
     * JPA creates a JOIN TABLE automatically: demo_order_tags
     *   Columns: order_id | tag_id
     *
     * FetchType.LAZY (default for @ManyToMany):
     *   Tags are NOT fetched with the order. Loaded only when getTags() is called.
     *
     * cascade = PERSIST, MERGE: saving an order also saves new tags,
     *   but does NOT delete tags when the order is deleted (tags are shared!).
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "demo_order_tags",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private java.util.Set<Tag> tags = new java.util.HashSet<>();

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
}
