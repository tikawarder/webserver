package databaseserver.postgresql.service;

import databaseserver.postgresql.dto.CustomerDto;
import databaseserver.postgresql.dto.OrderDto;
import databaseserver.postgresql.model.Customer;
import databaseserver.postgresql.model.Order;
import databaseserver.postgresql.repository.CustomerRepository;
import databaseserver.postgresql.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service demonstrating the four core PostgreSQL/JPA concepts:
 *
 *  1. ACID  — via @Transactional and the atomic transfer scenario
 *  2. JOINs — via JOIN FETCH queries (INNER and LEFT)
 *  3. INDEX — visible in entity @Table(indexes=...) and query patterns
 *  4. N+1   — broken vs. fixed implementations side by side
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresDemoService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    // =========================================================================
    // SEED DATA
    // =========================================================================

    /**
     * Populates the database with sample customers and orders.
     * Called automatically via DataInitializer on startup.
     */
    @Transactional
    public void seedData() {
        if (customerRepository.count() > 0) {
            log.info("[DEMO] Database already seeded, skipping.");
            return;
        }
        log.info("[DEMO] Seeding demo data...");

        Customer alice = Customer.builder()
                .name("Alice Weber").email("alice@demo.com").city("Budapest").build();
        Customer bob = Customer.builder()
                .name("Bob Kovacs").email("bob@demo.com").city("Debrecen").build();
        // Charlie has NO orders — important for LEFT JOIN vs INNER JOIN demo
        Customer charlie = Customer.builder()
                .name("Charlie Toth").email("charlie@demo.com").city("Budapest").build();

        customerRepository.saveAll(List.of(alice, bob, charlie));

        Order o1 = Order.builder().product("Laptop").amount(new BigDecimal("1200.00"))
                .status(Order.OrderStatus.DELIVERED).createdAt(LocalDateTime.now().minusDays(10))
                .customer(alice).build();
        Order o2 = Order.builder().product("Monitor").amount(new BigDecimal("350.00"))
                .status(Order.OrderStatus.SHIPPED).createdAt(LocalDateTime.now().minusDays(5))
                .customer(alice).build();
        Order o3 = Order.builder().product("Keyboard").amount(new BigDecimal("80.00"))
                .status(Order.OrderStatus.PENDING).createdAt(LocalDateTime.now().minusDays(1))
                .customer(bob).build();

        orderRepository.saveAll(List.of(o1, o2, o3));
        log.info("[DEMO] Seeding complete: 3 customers, 3 orders.");
    }

    // =========================================================================
    // 1. ACID DEMO
    // =========================================================================

    /**
     * ACID — Atomicity demo: "transfer" amount between two orders.
     *
     * Both the debit and credit happen inside a SINGLE @Transactional method.
     * If ANY step throws an exception, the ENTIRE operation is rolled back.
     * The database never ends up in a half-updated state.
     *
     * To see this in action:
     *   - Call /api/demo/acid/transfer?fromId=1&toId=2&amount=100  → success
     *   - Call /api/demo/acid/transfer-fail?fromId=1&toId=2&amount=100 → rollback
     */
    @Transactional
    public String atomicTransfer(Long fromOrderId, Long toOrderId, BigDecimal amount) {
        Order from = orderRepository.findById(fromOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Source order not found: " + fromOrderId));
        Order to = orderRepository.findById(toOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Target order not found: " + toOrderId));

        if (from.getAmount().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient amount in source order.");
        }

        // Step 1: debit
        from.setAmount(from.getAmount().subtract(amount));
        orderRepository.save(from);

        // Step 2: credit
        to.setAmount(to.getAmount().add(amount));
        orderRepository.save(to);

        return String.format(
            "ACID SUCCESS: Transferred %.2f from Order#%d to Order#%d. " +
            "Both updates committed atomically.",
            amount, fromOrderId, toOrderId
        );
    }

    /**
     * ACID — Rollback demo: simulates a failure AFTER the first DB write.
     *
     * Without @Transactional: Order#from would be debited, Order#to would NOT be credited.
     * WITH @Transactional: the debit is rolled back automatically when the exception occurs.
     */
    @Transactional
    public String atomicTransferWithRollback(Long fromOrderId, Long toOrderId, BigDecimal amount) {
        Order from = orderRepository.findById(fromOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + fromOrderId));

        // Step 1: debit happens (written to DB within transaction)
        from.setAmount(from.getAmount().subtract(amount));
        orderRepository.save(from);

        log.warn("[ACID ROLLBACK DEMO] Debit done. Now simulating a system failure...");

        // Step 2: simulated failure — triggers full transaction rollback
        throw new RuntimeException(
            "ACID ROLLBACK: Simulated failure after debit. " +
            "Check the DB — Order#" + fromOrderId + " was NOT permanently changed."
        );
    }

    // =========================================================================
    // 2. JOIN DEMOS
    // =========================================================================

    /**
     * INNER JOIN: Returns only customers who have at least one order.
     * Charlie (0 orders) will NOT appear in this result.
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> getCustomersWithOrdersInnerJoin() {
        log.info("[JOIN] INNER JOIN — customers with at least one order");
        List<Customer> customers = customerRepository.findCustomersWithAtLeastOneOrder();
        List<Order> allOrders = orderRepository.findAllWithCustomer();
        Map<Long, List<Order>> ordersByCustomerId = allOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getCustomer().getId()));
        return customers.stream()
                .map(c -> toDtoWithOrders(c, ordersByCustomerId.getOrDefault(c.getId(), List.of())))
                .collect(Collectors.toList());
    }

    /**
     * LEFT JOIN: Returns ALL customers, including those with zero orders.
     * Charlie (0 orders) WILL appear with an empty orders list.
     *
     * We load all orders once and group them by customer ID in memory.
     * This avoids the LAZY loading issue in the JPA session.
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomersLeftJoin() {
        log.info("[JOIN] LEFT JOIN — all customers including those with no orders");
        List<Customer> customers = customerRepository.findAllCustomersLeftJoin();
        // Load all orders in one query, group by customerId
        List<Order> allOrders = orderRepository.findAllWithCustomer();
        Map<Long, List<Order>> ordersByCustomerId = allOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getCustomer().getId()));
        return customers.stream()
                .map(c -> toDtoWithOrders(c, ordersByCustomerId.getOrDefault(c.getId(), List.of())))
                .collect(Collectors.toList());
    }

    // =========================================================================
    // 3. N+1 PROBLEM DEMOS
    // =========================================================================

    /**
     * BROKEN — N+1 problem.
     *
     * This fires 1 query to get all customers,
     * then 1 MORE query for EACH customer's orders.
     * With 100 customers: 101 queries. With 1000: 1001 queries.
     *
     * Watch the logs: you will see many SELECT statements.
     */
    @Transactional
    public List<CustomerDto> getCustomersN1Problem() {
        log.warn("[N+1 PROBLEM] Starting — watch the SQL log for multiple queries!");
        List<Customer> customers = customerRepository.findAllWithN1Problem();

        // This line triggers the N+1: for each customer, Hibernate fires a new query
        return customers.stream().map(customer -> {
            log.warn("[N+1 PROBLEM] Loading orders for customer: {}", customer.getName());
            // customer.getOrders() triggers a new SELECT per customer!
            return toDtoWithOrders(customer, customer.getOrders());
        }).collect(Collectors.toList());
    }

    /**
     * FIXED — JOIN FETCH solution.
     *
     * Fires exactly 1 SQL query with a JOIN.
     * No matter how many customers: always 1 query.
     *
     * Watch the logs: you will see only ONE SQL statement with a JOIN.
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> getCustomersFixed() {
        log.info("[N+1 FIXED] Using JOIN FETCH — single SQL query");
        List<Customer> customers = customerRepository.findAllWithOrdersJoinFetch();
        return customers.stream()
                .map(c -> toDtoWithOrders(c, c.getOrders()))
                .collect(Collectors.toList());
    }

    // =========================================================================
    // AGGREGATES
    // =========================================================================

    /**
     * Revenue grouped by city.
     * Demonstrates: JOIN + GROUP BY + SUM aggregate.
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> revenuePerCity() {
        return orderRepository.totalRevenuePerCity().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    /**
     * Order count grouped by customer name.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> orderCountPerCustomer() {
        return orderRepository.orderCountPerCustomer().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    // =========================================================================
    // MAPPER
    // =========================================================================

    /**
     * Maps a Customer entity and an explicit list of Orders to a DTO.
     * We pass orders explicitly to avoid LAZY loading surprises.
     */
    private CustomerDto toDtoWithOrders(Customer c, List<Order> orders) {
        List<OrderDto> orderDtos = orders.stream()
                .map(o -> OrderDto.builder()
                        .id(o.getId())
                        .product(o.getProduct())
                        .amount(o.getAmount())
                        .status(o.getStatus().name())
                        .createdAt(o.getCreatedAt())
                        .customerName(c.getName())
                        .customerCity(c.getCity())
                        .build())
                .collect(Collectors.toList());

        return CustomerDto.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .city(c.getCity())
                .orders(orderDtos)
                .build();
    }
}
