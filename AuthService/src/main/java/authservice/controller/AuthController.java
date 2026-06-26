package authservice.controller;

import authservice.model.entity.LoginRequest;
import authservice.services.security.JwtUtil;
import jakarta.validation.Valid;
import authservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "Login, logout and JWT validation")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountRepository accountRepository;

    @Operation(summary = "Validate JWT and return whether username exists (internal use)")
    @GetMapping("/validate/{username}")
    public ResponseEntity<Boolean> validateUser(@PathVariable String username) {
        boolean exists = accountRepository.findByUsername(username).isPresent();
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Logout — clears JWT cookie")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie clearCookie = ResponseCookie.from("jwt-token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body("Logged out.");
    }

    @Operation(summary = "Login — authenticates user and returns JWT in HttpOnly cookie")
    @PostMapping
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginRequest request) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String jwt = jwtUtil.generateToken(authentication.getName());

            ResponseCookie jwtCookie = ResponseCookie.from("jwt-token", jwt)
                    .httpOnly(true)
                    .secure(false)      //later when https exist, set to true
                    .path("/")
                    .maxAge(1 * 60 * 60) // age is for 1 hour
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body("Successfully login. The token is in the http cookie.");
    }
}