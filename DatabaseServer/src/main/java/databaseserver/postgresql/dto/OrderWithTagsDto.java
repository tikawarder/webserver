package databaseserver.postgresql.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Order DTO including its @ManyToMany tags.
 */
@Data
@Builder
public class OrderWithTagsDto {
    private Long id;
    private String product;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private String customerName;

    // @ManyToMany result
    private Set<String> tags;
}
