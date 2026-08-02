package com.mycloud.common_models.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class EncryptionUtil {
    private final String secret;
    private final SecretKeySpec key;

    public EncryptionUtil(String secret) {
        this.secret = secret;
        key = new SecretKeySpec(secret.getBytes(), "AES");
    }

    private Cipher GetEncryptCipher() throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher;
    }

    private Cipher GetDecryptCipher() throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher;
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

    public String EncryptHexEncoding(String value) {
        try {
            byte[] encrypted = GetEncryptCipher().doFinal(value.getBytes());
            return HexFormat.of().formatHex(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String DecryptHexEncoding(String value) {
        try {
            byte[] encrypted = HexFormat.of().parseHex(value);
            byte[] decrypted = GetDecryptCipher().doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}