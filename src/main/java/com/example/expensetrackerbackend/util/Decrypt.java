package com.example.expensetrackerbackend.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Decrypt {
    private static final String KEY = "1234567890123456";  // 16 chars

    public static String decrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }
}