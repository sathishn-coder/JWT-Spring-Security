package com.example.crud.jwtspringsecurity.service;



import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service — the heart of stateless authentication.
 *
 * How JWT works:
 * ┌─────────────────────────────────────────────────────────────┐
 * │  JWT = Header.Payload.Signature                             │
 * │  Header:    algorithm (HS256) + type (JWT)                  │
 * │  Payload:   claims (username, role, issued-at, expiry)      │
 * │  Signature: HMAC-SHA256(base64(header).base64(payload), key)│
 * └─────────────────────────────────────────────────────────────┘
 *
 * The server:
 *  1. Generates a signed token on login
 *  2. Verifies the signature on every subsequent request
 *  3. Never stores tokens (stateless) — the signature IS the proof
 */
@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // ─── Token Generation ────────────────────────────────────────────────────

    /**
     * Generate a token for a user with no extra claims.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate a token with additional claims (e.g. role, user ID).
     *
     * @param extraClaims  key-value pairs embedded in the JWT payload
     * @param userDetails  the authenticated user
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())   // "sub" claim
                .issuedAt(new Date())                 // "iat" claim
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // "exp" claim
                .signWith(getSigningKey())            // signs with HMAC-SHA256
                .compact();                           // serialize to compact string
    }

    // ─── Token Validation ────────────────────────────────────────────────────

    /**
     * Validate a token: checks signature, expiry, and username match.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ─── Claims Extraction ───────────────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic claim extractor — accepts any function to pull a specific claim.
     * Example: extractClaim(token, Claims::getSubject)
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parse the JWT and return all claims.
     * Throws a JwtException subclass if the token is invalid or expired.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ─── Key Management ──────────────────────────────────────────────────────

    /**
     * Derives a SecretKey from the hex-encoded secret in application.properties.
     * HS256 requires a key of at least 256 bits (32 bytes).
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
