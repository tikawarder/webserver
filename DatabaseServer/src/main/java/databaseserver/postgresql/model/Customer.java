package databaseserver.postgresql.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the PostgreSQL demo module.
 *
 * Relationships:
 *   Customer (1) --- (*) Order        @OneToMany  / @ManyToOne
 *   Customer (1) --- (1) Profile      @OneToOne
 *
 * FetchType.LAZY (default for @OneToMany, @ManyToOne):
 *   orders and profile are NOT loaded until explicitly accessed.
 */
@Entity
@Table(
    name = "demo_customers",
    indexes = {
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
     * @OneToMany — LAZY (default).
     * 'mappedBy' = the field name in Order that holds the FK.
     * cascade = ALL: saving/deleting Customer also saves/deletes its Orders.
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    /**
     * @OneToOne — LAZY (best practice).
     * 'mappedBy' = the field in CustomerProfile that owns the FK.
     * EAGER here would load the profile even when you only need the customer's name.
     */
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CustomerProfile profile;
}
