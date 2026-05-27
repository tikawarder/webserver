package databaseserver.postgresql.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an order placed by a Customer.
 *
 * Relationship: Order (*) --- (1) Customer
 *
 * Used to demonstrate:
 * - JOIN queries: how to fetch Customer + Orders together
 * - N+1 problem: when loading orders per customer in a loop
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
     * The owning side of the Customer <-> Order relationship.
     * The foreign key column 'customer_id' lives in this table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
}
