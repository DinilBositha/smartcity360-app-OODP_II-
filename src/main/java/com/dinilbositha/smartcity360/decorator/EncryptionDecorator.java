package com.dinilbositha.smartcity360.decorator;

import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


 // Concrete decorator adding AES encryption layer to the data stream.

public class EncryptionDecorator extends StreamDecorator {
    private static final String ALGO = "AES";
    private final byte[] key;

    public EncryptionDecorator(DataStream wrappee, byte[] key) {
        super(wrappee);
        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("AES key must be exactly 16 bytes");
        }
        this.key = key.clone();
    }

    public EncryptionDecorator(DataStream wrappee) {
        this(wrappee, "SmartCity360Key!".getBytes());
    }

    @Override
    public byte[] write(byte[] data) {
        byte[] processed = wrappee.write(data);
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGO));
            return cipher.doFinal(processed);
        } catch (Exception e) {
            throw new StreamException("Encryption failed", e);
        }
    }

    @Override
    public byte[] read(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGO));
            byte[] decrypted = cipher.doFinal(data);
            return wrappee.read(decrypted);
        } catch (Exception e) {
            throw new StreamException("Decryption failed", e);
        }
    }
}
