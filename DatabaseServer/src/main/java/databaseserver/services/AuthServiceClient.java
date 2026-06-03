package databaseserver.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthServiceClient {

    private final String authServiceUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthServiceClient(
            @Value("${auth.service.url:http://auth_service:8083/api/auth/validate/}") String authServiceUrl) {
        this.authServiceUrl = authServiceUrl;
    }

    @CircuitBreaker(name = "authServiceCB", fallbackMethod = "fallbackValidateUser")
    public boolean validateUser(String username) {
        log.info("[AuthServiceClient] Querying AuthService to validate user: {}", username);
        Boolean isValid = restTemplate.getForObject(authServiceUrl + username, Boolean.class);
        return isValid != null && isValid;
    }

    public boolean fallbackValidateUser(String username, Throwable t) {
        log.warn("[CircuitBreaker] Auth Service validation failed for user '{}'. Fallback triggered.", username);
        return true;
    }
}
