package com.mycloud.common_models.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

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
                .claim("uid", encryptedUserId)
                .claim("eml", encryptedEmail)
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
                        .get("eml", String.class);

        return encryptionUtil.Decrypt(
                encryptedEmail
        );
    }

    public Long ExtractUserId(String token) {
        String encryptedUserId =
                GetClaims(token)
                        .get("uid", String.class);

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
}