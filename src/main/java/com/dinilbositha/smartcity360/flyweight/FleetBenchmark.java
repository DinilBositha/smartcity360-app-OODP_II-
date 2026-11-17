package com.dinilbositha.smartcity360.flyweight;

public final class FleetBenchmark {

    private FleetBenchmark() {}

    public static final class MemoryReport {
        public final int totalLights;
        public final int uniqueFlyweights;
        public final long unoptimizedBytes;
        public final long flyweightBytes;
        public final double savingsPercent;

        public MemoryReport(int totalLights, int uniqueFlyweights, long unoptimizedBytes, long flyweightBytes) {
            this.totalLights = totalLights;
            this.uniqueFlyweights = uniqueFlyweights;
            this.unoptimizedBytes = unoptimizedBytes;
            this.flyweightBytes = flyweightBytes;
            this.savingsPercent = unoptimizedBytes > 0 
                    ? ((double) (unoptimizedBytes - flyweightBytes) / unoptimizedBytes) * 100.0 
                    : 0.0;
        }
    }


    public static MemoryReport analyze(int count, int uniqueTypes) {
        long without = (long) count * 84L;
        long with = ((long) uniqueTypes * 60L) + ((long) count * 24L);
        return new MemoryReport(count, uniqueTypes, without, with);
    }
}
