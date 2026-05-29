package authservice.services.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final Key secretKey;
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    public JwtUtil(@org.springframework.beans.factory.annotation.Value("${spring.security.jwt.secret:mySecretKeyForJWTTokenSigningMustBeAtLeast256BitsLongForHMACSHA256Algorithm}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

protected String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
}

private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
}

private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}

protected Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
}
}