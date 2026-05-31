package com.mycloud.common_models.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {
    private final String secret;

    public EncryptionUtil(String secret) {
        this.secret = secret;
    }

    public String Encrypt(String value) {
        try {
            SecretKeySpec key =
                    new SecretKeySpec(secret.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted =
                    cipher.doFinal(value.getBytes());

            return Base64.getEncoder()
                    .encodeToString(encrypted);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String Decrypt(String value) {
        try {
            SecretKeySpec key =
                    new SecretKeySpec(secret.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decrypted =
                    cipher.doFinal(
                            Base64.getDecoder().decode(value)
                    );

            return new String(decrypted);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}