package com.dinilbositha.smartcity360.decorator;

import java.util.Objects;


//Concrete component representing the base raw meter data stream.

public class MeterStream implements DataStream {

    @Override
    public byte[] write(byte[] data) {
        Objects.requireNonNull(data, "Data cannot be null");
        return data.clone();
    }

    @Override
    public byte[] read(byte[] data) {
        Objects.requireNonNull(data, "Data cannot be null");
        return data.clone();
    }
}
