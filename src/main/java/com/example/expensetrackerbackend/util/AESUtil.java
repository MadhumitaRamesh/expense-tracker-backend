package com.example.expensetrackerbackend.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    // 32-byte key for AES-256
    private static final String SECRET_KEY = "MySecretKey12345MySecretKey12345"; // 32 chars for AES-256
    
    /**
     * Decrypt data using AES-256-CBC
     * Expected format: Base64(IV + EncryptedData)
     */
    public static String decrypt(String encryptedData) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        
        // Extract IV (first 16 bytes)
        byte[] iv = new byte[16];
        System.arraycopy(decoded, 0, iv, 0, 16);
        
        // Extract encrypted content (remaining bytes)
        byte[] encryptedBytes = new byte[decoded.length - 16];
        System.arraycopy(decoded, 16, encryptedBytes, 0, decoded.length - 16);
        
        // Create cipher
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted, "UTF-8");
    }
    
    /**
     * Encrypt data using AES-256-CBC
     * Returns: Base64(IV + EncryptedData)
     */
    public static String encrypt(String data) throws Exception {
        // Generate random IV
        byte[] iv = new byte[16];
        new java.security.SecureRandom().nextBytes(iv);
        
        // Create cipher
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        
        byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
        
        // Combine IV + encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        
        return Base64.getEncoder().encodeToString(combined);
    }
}