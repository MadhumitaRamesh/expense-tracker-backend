package com.example.expensetrackerbackend.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Decrypt {
    private static final String KEY = "mysecretkey123"; // SAME AS FRONTEND

    public static String decrypt(String encrypted) throws Exception {
        byte[] keyBytes = KEY.getBytes("UTF-8");
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decrypted, "UTF-8");
    }
}