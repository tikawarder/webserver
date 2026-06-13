package databaseserver.postgresql.service;

import databaseserver.postgresql.dto.CustomerFullDto;
import databaseserver.postgresql.dto.OrderWithTagsDto;
import databaseserver.postgresql.model.*;
import databaseserver.postgresql.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service demonstrating all four JPA relationship types and FetchType behaviour.
 *
 *  @OneToOne   — Customer <-> CustomerProfile
 *  @OneToMany  — Customer -> List<Order>
 *  @ManyToOne  — Order -> Customer
 *  @ManyToMany — Order <-> Set<Tag>
 *
 *  FetchType.LAZY  — load only when accessed (default, best practice)
 *  FetchType.EAGER — load immediately, always (rarely the right choice)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RelationshipDemoService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProfileRepository profileRepository;
    private final TagOrderRepository tagOrderRepository;
    private final TagRepository tagRepository;

    // =========================================================================
    // SEED DATA — adds profiles and tags to existing customers/orders
    // =========================================================================

    @Transactional
    public void seedRelationshipData() {
        if (profileRepository.count() > 0) {
            log.info("[RELATIONS] Data already seeded.");
            return;
        }

        List<Customer> customers = customerRepository.findAll();
        if (customers.size() < 3) {
            log.warn("[RELATIONS] Need at least 3 customers. Run /api/demo/* first.");
            return;
        }

        Customer alice   = customers.get(0);
        Customer bob     = customers.get(1);
        Customer charlie = customers.get(2);

        // ----- @OneToOne: create profiles -----
        CustomerProfile aliceProfile = CustomerProfile.builder()
                .phone("+36-1-234-5678").bio("Senior buyer, loves gadgets.").avatarUrl("/avatars/alice.png")
                .customer(alice).build();
        CustomerProfile bobProfile = CustomerProfile.builder()
                .phone("+36-52-987-6543").bio("Budget-conscious shopper.").avatarUrl("/avatars/bob.png")
                .customer(bob).build();
        // Charlie has NO profile — important for @OneToOne LEFT JOIN demo
        profileRepository.saveAll(List.of(aliceProfile, bobProfile));

        // ----- @ManyToMany: create tags -----
        Tag electronics = new Tag(); electronics.setName("electronics");
        Tag urgent      = new Tag(); urgent.setName("urgent");
        Tag gift        = new Tag(); gift.setName("gift");

        // Save tags FIRST to avoid cascade transient ID unique constraint violations
        tagRepository.saveAll(List.of(electronics, urgent, gift));

        // Assign tags to orders (orders from the DB)
        List<Order> orders = orderRepository.findAll();
        if (orders.size() >= 3) {
            Order laptop   = orders.get(0); // Alice
            Order monitor  = orders.get(1); // Alice
            Order keyboard = orders.get(2); // Bob

            laptop.getTags().add(electronics);
            laptop.getTags().add(gift);

            monitor.getTags().add(electronics);
            monitor.getTags().add(urgent);

            keyboard.getTags().add(electronics);

            orderRepository.saveAll(List.of(laptop, monitor, keyboard));
        }
        log.info("[RELATIONS] Profiles and tags seeded.");
    }

    // =========================================================================
    // @OneToOne DEMO
    // =========================================================================

    /**
     * LAZY @OneToOne — loads customer, profile NOT loaded yet.
     *
     * The log shows:
     *   1. SELECT * FROM demo_customers WHERE id=?
     *   (profile NOT loaded — it's a proxy)
     *   2. SELECT * FROM demo_profiles WHERE customer_id=?  (only when getProfile() is called)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> demonstrateLazyOneToOne(Long customerId) {
        log.info("[ONE-TO-ONE LAZY] Loading customer #{}", customerId);
        // Step 1: only customer is loaded (1 SQL)
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        log.info("[ONE-TO-ONE LAZY] Customer loaded: {}. Profile NOT loaded yet (proxy).", customer.getName());
        log.info("[ONE-TO-ONE LAZY] Now accessing customer.getProfile() — triggers DB call!");

        // Step 2: this call triggers the second SQL (profile is LAZY)
        CustomerProfile profile = customer.getProfile();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerId",   customer.getId());
        result.put("customerName", customer.getName());
        result.put("explanation",  "2 SQL queries: 1 for customer, 1 for profile (LAZY triggered by getProfile())");

        if (profile != null) {
            result.put("phone",     profile.getPhone());
            result.put("bio",       profile.getBio());
            result.put("avatarUrl", profile.getAvatarUrl());
        } else {
            result.put("profile", "null — Charlie has no profile (useful for optional @OneToOne)");
        }
        return result;
    }

    /**
     * JOIN FETCH @OneToOne — loads customer AND profile in a single SQL.
     *
     * The log shows:
     *   1. SELECT c.*, p.* FROM demo_customers c LEFT JOIN demo_profiles p ON p.customer_id=c.id
     *   (everything in one shot)
     */
    @Transactional(readOnly = true)
    public List<CustomerFullDto> getAllCustomersWithProfileJoinFetch() {
        log.info("[ONE-TO-ONE JOIN FETCH] Loading all customers with profiles — single query");
        List<Customer> customers = customerRepository.findAllWithProfile();

        return customers.stream().map(c -> {
            CustomerProfile p = c.getProfile();
            CustomerFullDto.ProfileDto profileDto = (p == null) ? null :
                    CustomerFullDto.ProfileDto.builder()
                            .phone(p.getPhone()).bio(p.getBio()).avatarUrl(p.getAvatarUrl())
                            .build();
            return CustomerFullDto.builder()
                    .id(c.getId()).name(c.getName()).email(c.getEmail()).city(c.getCity())
                    .profile(profileDto)
                    .orders(List.of()) // orders not loaded here
                    .build();
        }).collect(Collectors.toList());
    }

    // =========================================================================
    // @ManyToMany DEMO
    // =========================================================================

    /**
     * All orders with their tags — loaded in a single JOIN FETCH query.
     *
     * The log shows ONE SQL with two JOINs:
     *   demo_orders → demo_order_tags → demo_tags
     */
    @Transactional(readOnly = true)
    public List<OrderWithTagsDto> getAllOrdersWithTags() {
        log.info("[MANY-TO-MANY] Loading all orders with tags via JOIN FETCH");
        List<Order> orders = tagOrderRepository.findAllWithTagsAndCustomer();
        return orders.stream().map(this::toOrderWithTagsDto).collect(Collectors.toList());
    }

    /**
     * Filter orders by tag — demonstrates ManyToMany WHERE on the join table.
     */
    @Transactional(readOnly = true)
    public List<OrderWithTagsDto> getOrdersByTag(String tagName) {
        log.info("[MANY-TO-MANY] Filtering orders by tag: {}", tagName);
        return tagOrderRepository.findByTagName(tagName)
                .stream().map(this::toOrderWithTagsDto).collect(Collectors.toList());
    }

    /**
     * Revenue per tag — ManyToMany JOIN + GROUP BY + SUM.
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> revenueByTag() {
        return tagOrderRepository.revenueByTag().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    // =========================================================================
    // FULL PICTURE — customer with profile AND orders AND tags
    // =========================================================================

    /**
     * Returns the full relationship tree for one customer:
     *   Customer → Profile (@OneToOne)
     *             → Orders (@OneToMany)
     *                  → Tags (@ManyToMany)
     *
     * All loaded in as few queries as possible.
     */
    @Transactional(readOnly = true)
    public CustomerFullDto getFullCustomer(Long customerId) {
        log.info("[FULL] Loading complete relationship tree for customer #{}", customerId);

        Customer customer = customerRepository.findByIdWithProfileAndOrders(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found: " + customerId));

        // Load tags for this customer's orders
        List<Order> ordersWithTags = tagOrderRepository.findAllWithTagsAndCustomer()
                .stream().filter(o -> o.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList());

        CustomerProfile p = customer.getProfile();
        CustomerFullDto.ProfileDto profileDto = (p == null) ? null :
                CustomerFullDto.ProfileDto.builder()
                        .phone(p.getPhone()).bio(p.getBio()).avatarUrl(p.getAvatarUrl())
                        .build();

        List<OrderWithTagsDto> orderDtos = ordersWithTags.stream()
                .map(this::toOrderWithTagsDto).collect(Collectors.toList());

        return CustomerFullDto.builder()
                .id(customer.getId()).name(customer.getName())
                .email(customer.getEmail()).city(customer.getCity())
                .profile(profileDto)
                .orders(orderDtos)
                .build();
    }

    // =========================================================================
    // FETCH TYPE COMPARISON
    // =========================================================================

    /**
     * Shows the EAGER vs LAZY difference side by side in the log.
     *
     * EAGER: even if you never call getProfile(), it is loaded.
     * LAZY:  profile is only loaded when you call getProfile().
     *
     * The EAGER query: SELECT c.*, p.* FROM demo_customers c LEFT JOIN demo_profiles p ...
     * The LAZY query:  SELECT * FROM demo_customers  (profile NOT in this query)
     */
    @Transactional(readOnly = true)
    public Map<String, String> explainFetchTypes() {
        Map<String, String> explanation = new LinkedHashMap<>();

        explanation.put("LAZY (default for @OneToMany, @ManyToOne, @ManyToMany)",
            "Hibernate creates a PROXY object instead of loading data. " +
            "Real SQL fires only when you access the field. " +
            "RISK: LazyInitializationException if accessed outside a transaction.");

        explanation.put("EAGER (default for @OneToOne, @ManyToOne in older specs)",
            "Hibernate loads the related entity ALWAYS, in the SAME query (JOIN). " +
            "Even if you never need it. " +
            "RISK: loads too much data, slows down queries.");

        explanation.put("Best practice",
            "Always use LAZY. Use JOIN FETCH or @EntityGraph only when you KNOW you need the data.");

        explanation.put("LazyInitializationException",
            "Happens when you access a LAZY field OUTSIDE a @Transactional method. " +
            "The Hibernate session is closed, the proxy cannot load. " +
            "Fix: keep access inside @Transactional, or use JOIN FETCH.");

        explanation.put("N+1 and LAZY connection",
            "N+1 is caused by LAZY + accessing the field in a LOOP. " +
            "Each loop iteration fires a new SQL. " +
            "Fix: JOIN FETCH or @EntityGraph loads everything in one query.");

        return explanation;
    }

    // =========================================================================
    // MAPPER
    // =========================================================================

    private OrderWithTagsDto toOrderWithTagsDto(Order o) {
        Set<String> tagNames = o.getTags().stream()
                .map(Tag::getName).collect(Collectors.toSet());
        return OrderWithTagsDto.builder()
                .id(o.getId()).product(o.getProduct()).amount(o.getAmount())
                .status(o.getStatus().name()).createdAt(o.getCreatedAt())
                .customerName(o.getCustomer().getName())
                .tags(tagNames)
                .build();
    }
}
