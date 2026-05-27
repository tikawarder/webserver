package databaseserver.postgresql.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for Order data exposed via REST.
 * Keeps JPA entities out of the API layer (separation of concerns).
 */
@Data
@Builder
public class OrderDto {
    private Long id;
    private String product;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private String customerName;
    private String customerCity;
}
