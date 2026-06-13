package databaseserver.postgresql.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Full customer DTO including profile and orders.
 * Shows the complete relationship tree in one response.
 */
@Data
@Builder
public class CustomerFullDto {
    private Long id;
    private String name;
    private String email;
    private String city;

    // @OneToOne result
    private ProfileDto profile;

    // @OneToMany result
    private List<OrderWithTagsDto> orders;

    @Data
    @Builder
    public static class ProfileDto {
        private String phone;
        private String bio;
        private String avatarUrl;
    }
}
