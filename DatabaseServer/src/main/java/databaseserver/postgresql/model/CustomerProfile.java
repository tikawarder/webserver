package databaseserver.postgresql.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Demonstrates @OneToOne relationship.
 *
 * Every Customer has exactly one Profile (and vice versa).
 * The FK column "customer_id" lives here (in the "owned" side).
 *
 * FetchType comparison:
 *   - EAGER: Profile is loaded TOGETHER with Customer, always, even if unused.
 *   - LAZY:  Profile is loaded only when you call customer.getProfile().
 *
 * We use LAZY here (best practice) and show EAGER as a demo via a separate endpoint.
 */
@Entity
@Table(name = "demo_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;
    private String bio;
    private String avatarUrl;

    /**
     * @OneToOne — the owning side (has the FK column).
     *
     * FetchType.LAZY: Hibernate does NOT load the customer when you load the profile.
     * It creates a proxy object instead, and hits the DB only when you call getCustomer().
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", unique = true, nullable = false)
    private Customer customer;
}
