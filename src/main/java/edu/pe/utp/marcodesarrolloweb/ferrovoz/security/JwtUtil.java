package edu.pe.utp.marcodesarrolloweb.ferrovoz.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:defaultSecretKeyWithAtLeast256BitsForSecurityToPassHS256Signature}")
    private String secret;

    @Value("${app.jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).toList());
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getKey())
            .compact();
    }

    public String extraerUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean esValido(String token, UserDetails userDetails) {
        final String username = extraerUsername(token);
        return username.equals(userDetails.getUsername())
            && !getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload();
    }
}
