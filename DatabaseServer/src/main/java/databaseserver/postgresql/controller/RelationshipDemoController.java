package databaseserver.postgresql.controller;

import databaseserver.postgresql.dto.CustomerFullDto;
import databaseserver.postgresql.dto.OrderWithTagsDto;
import databaseserver.postgresql.service.RelationshipDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints demonstrating all four JPA relationship types.
 *
 * BASE URL: http://localhost:8081/api/relations
 *
 * --- SEED ---
 * POST /api/relations/seed                          → seed profiles and tags
 *
 * --- @OneToOne ---
 * GET  /api/relations/one-to-one/lazy/{id}          → LAZY load demo (watch SQL log!)
 * GET  /api/relations/one-to-one/join-fetch         → JOIN FETCH all customers+profiles
 *
 * --- @ManyToMany ---
 * GET  /api/relations/many-to-many/orders           → all orders with their tags
 * GET  /api/relations/many-to-many/orders?tag=electronics  → filter by tag
 * GET  /api/relations/many-to-many/revenue-by-tag   → SUM grouped by tag
 *
 * --- FULL PICTURE ---
 * GET  /api/relations/full/{id}                     → customer + profile + orders + tags
 *
 * --- THEORY ---
 * GET  /api/relations/fetch-types                   → LAZY vs EAGER explanation
 */
@RestController
@RequestMapping("/api/relations")
@RequiredArgsConstructor
public class RelationshipDemoController {

    private final RelationshipDemoService service;

    @GetMapping("/seed")
    public ResponseEntity<String> seed() {
        service.seedRelationshipData();
        return ResponseEntity.ok("Profiles and tags seeded. Now call the other endpoints.");
    }

    // --- @OneToOne ---

    @GetMapping("/one-to-one/lazy/{id}")
    public ResponseEntity<Map<String, Object>> lazyOneToOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.demonstrateLazyOneToOne(id));
    }

    @GetMapping("/one-to-one/join-fetch")
    public ResponseEntity<List<CustomerFullDto>> joinFetchOneToOne() {
        return ResponseEntity.ok(service.getAllCustomersWithProfileJoinFetch());
    }

    // --- @ManyToMany ---

    @GetMapping("/many-to-many/orders")
    public ResponseEntity<List<OrderWithTagsDto>> ordersWithTags(
            @RequestParam(required = false) String tag) {
        if (tag != null) {
            return ResponseEntity.ok(service.getOrdersByTag(tag));
        }
        return ResponseEntity.ok(service.getAllOrdersWithTags());
    }

    @GetMapping("/many-to-many/revenue-by-tag")
    public ResponseEntity<Map<String, BigDecimal>> revenueByTag() {
        return ResponseEntity.ok(service.revenueByTag());
    }

    // --- Full relationship tree ---

    @GetMapping("/full/{id}")
    public ResponseEntity<CustomerFullDto> fullCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFullCustomer(id));
    }

    // --- Theory ---

    @GetMapping("/fetch-types")
    public ResponseEntity<Map<String, String>> fetchTypes() {
        return ResponseEntity.ok(service.explainFetchTypes());
    }
}
