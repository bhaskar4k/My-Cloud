package com.mycloud.common_models.utils;

import com.mycloud.common_models.common_entities.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.util.Date;


public class JwtUtil {
    private final long expiration;
    private final SecretKey key;
    private final EncryptionUtil encryptionUtil;

    public JwtUtil(String secret, long expiration) {
        this.expiration = expiration;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());

        // reuse same secret for AES encryption
        this.encryptionUtil = new EncryptionUtil(secret);
    }

    public String GenerateToken(Long userId, String email) {
        String encryptedUserId = encryptionUtil.Encrypt(String.valueOf(userId));
        String encryptedEmail = encryptionUtil.Encrypt(email);

        return Jwts.builder()
                .claim("enc_id", encryptedUserId)
                .claim("enc_email", encryptedEmail)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(key)
                .compact();
    }

    public String ExtractEmail(String token) {
        String encryptedEmail =
                GetClaims(token)
                        .get("enc_email", String.class);

        return encryptionUtil.Decrypt(
                encryptedEmail
        );
    }

    public Long ExtractUserId(String token) {
        String encryptedUserId =
                GetClaims(token)
                        .get("enc_id", String.class);

        return Long.parseLong(
                encryptionUtil.Decrypt(
                        encryptedUserId
                )
        );
    }

    public boolean ValidateToken(String token) {
        try {

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims GetClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static JwtUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof JwtUser jwtUser) {
            return jwtUser;
        }

        return null;
    }
}