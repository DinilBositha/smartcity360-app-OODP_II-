package com.dinilbositha.smartcity360.decorator;

import java.util.Objects;


 // Abstract decorator wrapping a DataStream instance.

public abstract class StreamDecorator implements DataStream {
    protected final DataStream wrappee;

    protected StreamDecorator(DataStream wrappee) {
        this.wrappee = Objects.requireNonNull(wrappee, "Wrapped stream cannot be null");
    }

    @Override
    public byte[] write(byte[] data) {
        return wrappee.write(data);
    }

    @Override
    public byte[] read(byte[] data) {
        return wrappee.read(data);
    }
}
