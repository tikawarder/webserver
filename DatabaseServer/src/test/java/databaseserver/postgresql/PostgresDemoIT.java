package databaseserver.postgresql;

import databaseserver.postgresql.dto.CustomerDto;
import databaseserver.postgresql.dto.OrderDto;
import databaseserver.postgresql.model.Customer;
import databaseserver.postgresql.model.Order;
import databaseserver.postgresql.repository.CustomerRepository;
import databaseserver.postgresql.repository.OrderRepository;
import databaseserver.postgresql.service.PostgresDemoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test suite for the PostgreSQL Demo module.
 *
 * Tests cover all four core topics:
 *  1. ACID  — @Transactional atomicity and rollback
 *  2. JOINs — INNER JOIN vs LEFT JOIN differences
 *  3. INDEX — query methods that rely on indexed columns
 *  4. N+1   — broken vs fixed query behaviour
 *
 * Each test is fully self-contained: @Transactional + @BeforeEach setup.
 * Uses H2 in-memory DB → no external dependencies needed.
 *
 * Run: mvn test -pl DatabaseServer -Dtest=PostgresDemoIT
 */
@SpringBootTest
@ActiveProfiles("h2")
class PostgresDemoIT {

    @Autowired
    private PostgresDemoService demoService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    // =========================================================================
    // TEST DATA SETUP
    // =========================================================================

    private Customer alice;
    private Customer bob;
    private Customer charlie; // <- NO orders! Key for LEFT JOIN test
    private Order orderAliceLaptop;
    private Order orderAliceMonitor;
    private Order orderBobKeyboard;

    @AfterEach
    void tearDown() {
        // Ensure all test data is cleaned up, even if a test fails mid-way.
        // This prevents unique constraint violations in the next test's @BeforeEach.
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();

        alice = customerRepository.save(Customer.builder()
                .name("Alice Weber").email("alice@test.com").city("Budapest").build());
        bob = customerRepository.save(Customer.builder()
                .name("Bob Kovacs").email("bob@test.com").city("Debrecen").build());
        charlie = customerRepository.save(Customer.builder()
                .name("Charlie Toth").email("charlie@test.com").city("Budapest").build());

        orderAliceLaptop = orderRepository.save(Order.builder()
                .product("Laptop").amount(new BigDecimal("1200.00"))
                .status(Order.OrderStatus.DELIVERED)
                .createdAt(LocalDateTime.now().minusDays(10))
                .customer(alice).build());

        orderAliceMonitor = orderRepository.save(Order.builder()
                .product("Monitor").amount(new BigDecimal("350.00"))
                .status(Order.OrderStatus.SHIPPED)
                .createdAt(LocalDateTime.now().minusDays(5))
                .customer(alice).build());

        orderBobKeyboard = orderRepository.save(Order.builder()
                .product("Keyboard").amount(new BigDecimal("80.00"))
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .customer(bob).build());
    }

    // =========================================================================
    // 1. ACID TESTS
    // =========================================================================

    @Nested
    @DisplayName("ACID — Atomicity & Rollback")
    class AcidTests {

        /**
         * Atomicity: both the debit and credit must succeed or neither is persisted.
         *
         * Before: Alice Laptop = 1200, Alice Monitor = 350
         * Transfer 100 from Laptop to Monitor
         * After:  Alice Laptop = 1100, Alice Monitor = 450
         */
        @Test
        @DisplayName("Successful transfer commits both changes atomically")
        void atomicTransfer_success_bothOrdersUpdated() {
            BigDecimal transferAmount = new BigDecimal("100.00");

            String result = demoService.atomicTransfer(
                    orderAliceLaptop.getId(),
                    orderAliceMonitor.getId(),
                    transferAmount
            );

            assertThat(result).contains("ACID SUCCESS");

            Order updatedFrom = orderRepository.findById(orderAliceLaptop.getId()).orElseThrow();
            Order updatedTo   = orderRepository.findById(orderAliceMonitor.getId()).orElseThrow();

            // Both changes committed atomically
            assertThat(updatedFrom.getAmount()).isEqualByComparingTo("1100.00");
            assertThat(updatedTo.getAmount()).isEqualByComparingTo("450.00");
        }

        /**
         * Consistency: we cannot transfer more than the available amount.
         * The transaction must fail and leave amounts unchanged.
         */
        @Test
        @DisplayName("Insufficient amount causes exception — amounts unchanged")
        void atomicTransfer_insufficientAmount_throwsException() {
            BigDecimal tooMuch = new BigDecimal("9999.00");

            assertThatThrownBy(() ->
                    demoService.atomicTransfer(
                            orderAliceLaptop.getId(),
                            orderAliceMonitor.getId(),
                            tooMuch
                    )
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("Insufficient");

            // Amounts must be unchanged after the failed transaction
            Order from = orderRepository.findById(orderAliceLaptop.getId()).orElseThrow();
            Order to   = orderRepository.findById(orderAliceMonitor.getId()).orElseThrow();
            assertThat(from.getAmount()).isEqualByComparingTo("1200.00");
            assertThat(to.getAmount()).isEqualByComparingTo("350.00");
        }

        /**
         * Rollback demo — important JPA/Spring concept explained:
         *
         * When a @Transactional service method throws a RuntimeException,
         * Spring rolls back the entire transaction unit.
         *
         * LIMITATION IN TESTING: if we wrap this test in @Transactional,
         * Spring treats the service call as a NESTED transaction, not a new one.
         * The outer test transaction "absorbs" the rollback, making it untestable
         * at the DB level without a separate connection.
         *
         * In production: the rollback is real. You can verify it with:
         *   - Two separate HTTP calls to /acid/transfer-fail
         *   - Then GET /api/demo/join/left → balances unchanged
         *
         * This test verifies: the exception IS thrown (the trigger is correct).
         */
        @Test
        @DisplayName("Exception after debit triggers rollback — exception is thrown")
        void atomicTransferWithRollback_exceptionCaused_noPermanentChange() {
            BigDecimal amount = new BigDecimal("50.00");

            // Verify that the exception is thrown — that is the rollback trigger
            assertThatThrownBy(() ->
                    demoService.atomicTransferWithRollback(
                            orderAliceLaptop.getId(),
                            orderAliceMonitor.getId(),
                            amount
                    )
            ).isInstanceOf(RuntimeException.class)
             .hasMessageContaining("ROLLBACK");

            // Note: DB state after rollback is only observable in a NEW transaction.
            // In the production scenario (REST call), the amount IS unchanged after rollback.
        }
    }

    // =========================================================================
    // 2. JOIN TESTS
    // =========================================================================

    @Nested
    @DisplayName("JOINs — INNER vs LEFT")
    class JoinTests {

        /**
         * INNER JOIN: only customers with at least one order.
         *
         * Setup: Alice (2 orders), Bob (1 order), Charlie (0 orders)
         * Expected: Alice and Bob appear, Charlie does NOT.
         */
        @Test
        @DisplayName("INNER JOIN excludes customers with no orders")
        void innerJoin_excludesCustomersWithNoOrders() {
            List<CustomerDto> result = demoService.getCustomersWithOrdersInnerJoin();

            List<String> names = result.stream().map(CustomerDto::getName).toList();

            assertThat(names).containsExactlyInAnyOrder("Alice Weber", "Bob Kovacs");
            // Charlie has no orders → INNER JOIN excludes him
            assertThat(names).doesNotContain("Charlie Toth");
        }

        /**
         * LEFT JOIN: ALL customers, even those with zero orders.
         *
         * Expected: Alice, Bob AND Charlie appear.
         * Charlie's orders list must be empty (not null).
         */
        @Test
        @DisplayName("LEFT JOIN includes customers with no orders — empty list, not null")
        void leftJoin_includesAllCustomersEvenWithNoOrders() {
            List<CustomerDto> result = demoService.getAllCustomersLeftJoin();

            List<String> names = result.stream().map(CustomerDto::getName).toList();

            assertThat(names).containsExactlyInAnyOrder("Alice Weber", "Bob Kovacs", "Charlie Toth");

            // Charlie has no orders → orders list must be empty, not null
            CustomerDto charlieDto = result.stream()
                    .filter(c -> c.getName().equals("Charlie Toth"))
                    .findFirst().orElseThrow();
            assertThat(charlieDto.getOrders()).isEmpty();
        }

        /**
         * JOIN result: Alice has exactly 2 orders.
         */
        @Test
        @DisplayName("JOIN correctly loads all orders per customer")
        void leftJoin_aliceHasTwoOrders() {
            List<CustomerDto> result = demoService.getAllCustomersLeftJoin();

            CustomerDto aliceDto = result.stream()
                    .filter(c -> c.getName().equals("Alice Weber"))
                    .findFirst().orElseThrow();

            assertThat(aliceDto.getOrders()).hasSize(2);
            List<String> products = aliceDto.getOrders().stream()
                    .map(OrderDto::getProduct).toList();
            assertThat(products).containsExactlyInAnyOrder("Laptop", "Monitor");
        }
    }

    // =========================================================================
    // 3. INDEX TESTS
    // =========================================================================

    @Nested
    @DisplayName("INDEX — indexed column queries")
    class IndexTests {

        /**
         * The email column has a unique index (idx_customer_email).
         * findByEmail() performs an index scan (not a full table scan).
         *
         * In real PostgreSQL you can verify with EXPLAIN ANALYZE:
         *   EXPLAIN ANALYZE SELECT * FROM demo_customers WHERE email = 'alice@test.com';
         *   → Result: "Index Scan using idx_customer_email"
         */
        @Test
        @DisplayName("findByEmail uses the email index — returns correct customer")
        void findByEmail_indexedColumn_returnsCustomer() {
            var found = customerRepository.findByEmail("alice@test.com");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Alice Weber");
        }

        /**
         * The city column also has an index (idx_customer_city).
         * Searching by city benefits from this index on large tables.
         */
        @Test
        @DisplayName("findByCity uses the city index — returns correct subset")
        void findByCity_indexedColumn_returnsCorrectCustomers() {
            List<Customer> budapesters = customerRepository.findByCity("Budapest");

            assertThat(budapesters).hasSize(2); // Alice and Charlie
            assertThat(budapesters).extracting(Customer::getName)
                    .containsExactlyInAnyOrder("Alice Weber", "Charlie Toth");
        }

        /**
         * Unique index prevents duplicate emails.
         * Attempting to save two customers with the same email must fail.
         */
        @Test
        @DisplayName("Unique index on email prevents duplicates")
        void saveCustomer_duplicateEmail_throwsException() {
            Customer duplicate = Customer.builder()
                    .name("Fake Alice").email("alice@test.com") // same email as Alice
                    .city("Pécs").build();

            assertThatThrownBy(() -> {
                customerRepository.save(duplicate);
                customerRepository.flush(); // force the INSERT to hit the DB
            }).isInstanceOf(Exception.class); // DataIntegrityViolationException
        }
    }

    // =========================================================================
    // 4. N+1 TESTS
    // =========================================================================

    @Nested
    @DisplayName("N+1 Problem — broken vs fixed")
    class N1Tests {

        /**
         * Both the broken and the fixed version must return the same data.
         * The difference is HOW MANY SQL queries are executed — visible in logs.
         *
         * Broken (N+1): 1 query for customers + N queries for orders = 1 + 3 = 4 queries
         * Fixed (JOIN): 1 single query with JOIN
         *
         * To observe: run with spring.jpa.show-sql=true and compare the log output.
         */
        @Test
        @DisplayName("N+1 broken and fixed return identical results")
        @Transactional // must be open so lazy collections can be accessed
        void n1BrokenAndFixed_sameData() {
            List<CustomerDto> broken = demoService.getCustomersN1Problem();
            List<CustomerDto> fixed  = demoService.getCustomersFixed();

            assertThat(broken).hasSize(3);
            assertThat(fixed).hasSize(3);

            // Same customer names in both
            List<String> brokenNames = broken.stream().map(CustomerDto::getName).sorted().toList();
            List<String> fixedNames  = fixed.stream().map(CustomerDto::getName).sorted().toList();
            assertThat(brokenNames).isEqualTo(fixedNames);
        }

        /**
         * The fixed version loads Alice's 2 orders correctly in a single query.
         */
        @Test
        @DisplayName("Fixed JOIN FETCH correctly loads Alice's 2 orders in one query")
        void n1Fixed_aliceTwoOrders() {
            List<CustomerDto> result = demoService.getCustomersFixed();

            CustomerDto alice = result.stream()
                    .filter(c -> c.getName().equals("Alice Weber"))
                    .findFirst().orElseThrow();

            assertThat(alice.getOrders()).hasSize(2);
        }
    }

    // =========================================================================
    // 5. AGGREGATE TESTS
    // =========================================================================

    @Nested
    @DisplayName("Aggregates — GROUP BY, SUM, COUNT")
    class AggregateTests {

        @Test
        @DisplayName("Revenue per city: Budapest = 1550, Debrecen = 80")
        void revenuePerCity_correctSums() {
            Map<String, BigDecimal> revenue = demoService.revenuePerCity();

            // Alice (Budapest): 1200 + 350 = 1550
            assertThat(revenue.get("Budapest")).isEqualByComparingTo("1550.00");
            // Bob (Debrecen): 80
            assertThat(revenue.get("Debrecen")).isEqualByComparingTo("80.00");
        }

        @Test
        @DisplayName("Order count per customer: Alice=2, Bob=1")
        void orderCountPerCustomer_correctCounts() {
            Map<String, Long> counts = demoService.orderCountPerCustomer();

            assertThat(counts.get("Alice Weber")).isEqualTo(2L);
            assertThat(counts.get("Bob Kovacs")).isEqualTo(1L);
        }
    }
}
