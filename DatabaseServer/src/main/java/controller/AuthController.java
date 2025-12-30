package controller;

import model.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping
    public boolean checkAuth(@RequestBody LoginRequest request) {
        String validUsername = "admin";
        String validPassword = "password";

        return validUsername.equals(request.getUsername()) &&
                validPassword.equals(request.getPassword());
    }
}
