package databaseserver.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "username can not be blank.")
    private String username;
    @NotBlank(message = "password can not be blank")
    private String password;
}