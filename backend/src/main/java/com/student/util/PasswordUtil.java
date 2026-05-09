package com.student.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类 - 使用 SHA-256 + 盐值进行密码哈希
 */
public class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = ":";

    /**
     * 对密码进行哈希处理（生成新盐值）
     * @param plainPassword 明文密码
     * @return 格式: salt:hashedPassword (Base64编码)
     */
    public static String hashPassword(String plainPassword) {
        byte[] salt = generateSalt();
        String hashedPassword = hash(plainPassword, salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        return saltBase64 + SEPARATOR + hashedPassword;
    }

    /**
     * 验证密码是否匹配
     * @param plainPassword 明文密码
     * @param storedPassword 存储的密码（格式: salt:hashedPassword）
     * @return 是否匹配
     */
    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        
        // 兼容旧的明文密码（迁移期间）
        if (!storedPassword.contains(SEPARATOR)) {
            return plainPassword.equals(storedPassword);
        }
        
        try {
            String[] parts = storedPassword.split(SEPARATOR);
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String storedHash = parts[1];
            String computedHash = hash(plainPassword, salt);
            
            return storedHash.equals(computedHash);
        } catch (Exception e) {
            logger.error("Password verification failed", e);
            return false;
        }
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static String hash(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
