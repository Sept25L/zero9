package com.zero9.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

public class JwtUtils {

    private static final Long expireTime = 60 * 60 * 1000 * 12L;

    private static final String secret = "zero9";

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static SecretKey getKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "HmacSHA256");
        } catch (Exception e) {
            throw new RuntimeException("生成密钥失败", e);
        }
    }

    /**
     * 创建 JWT（默认过期时间）
     */
    public static String createJWT(String subject) {
        return getJwtBuilder(subject, expireTime, getUUID());
    }

    /**
     * 创建 JWT（自定义过期时间）
     */
    public static String createJWT(String subject, Long jwtExpire) {
        return getJwtBuilder(subject, expireTime, getUUID());
    }

    /**
     * 创建 JWT（自定义过期 + 自定义 ID）
     */
    public static String createJWT(String subject, Long jwtExpire, String uuid) {
        return getJwtBuilder(subject, expireTime, uuid);
    }

    public static String getJwtBuilder(String subject, Long expireTime, String uuid) {

        long expireMillis = expireTime * 60 * 1000; // 分钟 → 毫秒

        return Jwts.builder()
                .id(getUUID())
                .subject(subject)
                .issuer("zero9")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(getKey())
                .compact();
    }

    public static Claims parseJWT(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token).
                getPayload();
    }
}
