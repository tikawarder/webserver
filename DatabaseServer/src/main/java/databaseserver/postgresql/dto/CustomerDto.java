package databaseserver.postgresql.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response DTO for Customer data exposed via REST.
 * Contains the orders list to show the full Customer → Orders relationship.
 */
@Data
@Builder
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String city;
    private List<OrderDto> orders;
}
