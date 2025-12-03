package com.example.expensetrackerbackend.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
  private static final String KEY = "supersecretbabyenckey"; // Same key as frontend
  private static final String ALGO = "AES";

  public static String decrypt(String strToDecrypt) throws Exception {
    SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGO);
    Cipher cipher = Cipher.getInstance(ALGO);
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt))); // Decrypt payload (boss's encryption)
  }
}