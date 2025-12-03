// src/main/java/com/example/expensetrackerbackend/util/Decrypt.java
package com.example.expensetrackerbackend.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Decrypt {
    private static final String KEY = "mysecretkey123";  // MUST match frontend

    public static String decrypt(String encryptedData) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            System.err.println("Decryption failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid encrypted data");
        }
    }
}