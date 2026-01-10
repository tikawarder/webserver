package databaseserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "username can not be empty")
    @Size(min = 3, max = 50, message = "username length must be 3 to 50 chars")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "password can not be empty")
    @Size(min = 3, max = 50, message = "password length must be 3 to 50 chars")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "role can not be empty")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}