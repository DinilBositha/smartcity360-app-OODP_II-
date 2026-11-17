package com.dinilbositha.smartcity360.decorator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


 // Concrete decorator adding GZIP compression layer to the data stream.

public class CompressionDecorator extends StreamDecorator {

    public CompressionDecorator(DataStream wrappee) {
        super(wrappee);
    }

    @Override
    public byte[] write(byte[] data) {
        byte[] processed = wrappee.write(data);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(processed);
            gzos.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new StreamException("Compression failed", e);
        }
    }

    @Override
    public byte[] read(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             GZIPInputStream gzis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return wrappee.read(baos.toByteArray());
        } catch (Exception e) {
            throw new StreamException("Decompression failed", e);
        }
    }
}
