package databaseserver.postgresql.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Demonstrates @ManyToMany relationship.
 *
 * An Order can have multiple Tags ("electronics", "urgent", "gift").
 * A Tag can belong to multiple Orders.
 *
 * JPA creates a JOIN TABLE automatically: demo_order_tags
 *   Columns: order_id | tag_id
 *
 * FetchType.LAZY (default for @ManyToMany):
 * Tags are NOT loaded when you load an Order.
 * Only loaded when order.getTags() is called.
 */
@Entity
@Table(name = "demo_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * @ManyToMany — the non-owning (inverse) side.
     * "mappedBy" points to the field name in Order that owns the relationship.
     * This side does NOT have the @JoinTable — Order does.
     */
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();
}
