package databaseserver.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    
    // Képes környezeti változóból olvasni, így Dockerben és lokálisan is kiválóan működik!
    private static final String AUTH_SERVICE_URL = System.getenv("AUTH_SERVICE_URL") != null 
            ? System.getenv("AUTH_SERVICE_URL") 
            : "http://auth_service:8083/api/auth/validate/";

    @CircuitBreaker(name = "authServiceCB", fallbackMethod = "fallbackValidateUser")
    public boolean validateUser(String username) {
        log.info("[AuthServiceClient] Querying AuthService to validate user: {}", username);
        String url = AUTH_SERVICE_URL + username;
        Boolean isValid = restTemplate.getForObject(url, Boolean.class);
        return isValid != null && isValid;
    }

    // A Fallback metódusnak ugyanazt az argumentumot és visszatérési típust kell kapnia, kiegészítve a hibával!
    public boolean fallbackValidateUser(String username, Throwable t) {
        log.warn("[CircuitBreaker] Auth Service validation failed for user '{}'! Fallback triggered. Error: {}", username, t.getMessage());
        // Fallback irányelv: mivel a JWT tokent a szűrő már kriptográfiailag sikeresen ellenőrizte,
        // engedélyezzük a kérést, de naplózzuk a kiesést figyelmeztetéssel.
        return true; 
    }
}
