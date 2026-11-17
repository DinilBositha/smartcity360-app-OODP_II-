package com.dinilbositha.smartcity360.decorator;

import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


 //Concrete decorator adding HMAC-SHA256 signature verification to ensure stream integrity.

public class AuthenticationDecorator extends StreamDecorator {
    private static final String HMAC_ALGO = "HmacSHA256";
    private final byte[] secret;

    public AuthenticationDecorator(DataStream wrappee, byte[] secret) {
        super(wrappee);
        this.secret = secret != null ? secret.clone() : "SmartCityHmacAuth".getBytes();
    }

    public AuthenticationDecorator(DataStream wrappee) {
        this(wrappee, null);
    }

    @Override
    public byte[] write(byte[] data) {
        byte[] processed = wrappee.write(data);
        byte[] mac = computeMac(processed);

        ByteBuffer buffer = ByteBuffer.allocate(mac.length + processed.length);
        buffer.put(mac);
        buffer.put(processed);
        return buffer.array();
    }

    @Override
    public byte[] read(byte[] data) {
        if (data.length < 32) {
            throw new StreamException("Data too short for HMAC verification");
        }
        byte[] mac = Arrays.copyOfRange(data, 0, 32);
        byte[] payload = Arrays.copyOfRange(data, 32, data.length);

        byte[] expectedMac = computeMac(payload);
        if (!Arrays.equals(mac, expectedMac)) {
            throw new StreamException("Stream authentication failed: invalid signature");
        }
        return wrappee.read(payload);
    }

    private byte[] computeMac(byte[] payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            return mac.doFinal(payload);
        } catch (Exception e) {
            throw new StreamException("MAC calculation failed", e);
        }
    }
}
