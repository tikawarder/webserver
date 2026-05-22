package databaseserver.controller;

import databaseserver.model.entity.LoginRequest;
import databaseserver.services.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:9080", "http://localhost:3000", "http://localhost:8080", "https://frontend-react-801953368913.us-east1.run.app", "https://dev.birotamas.hu"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

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