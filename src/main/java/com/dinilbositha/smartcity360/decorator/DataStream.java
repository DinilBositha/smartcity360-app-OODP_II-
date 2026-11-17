package com.dinilbositha.smartcity360.decorator;


 // Core interface for data stream processors in the telemetry pipeline.

public interface DataStream {
    byte[] write(byte[] data);
    byte[] read(byte[] data);
}
