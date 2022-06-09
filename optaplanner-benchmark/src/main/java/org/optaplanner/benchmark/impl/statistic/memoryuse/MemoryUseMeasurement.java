package org.optaplanner.benchmark.impl.statistic.memoryuse;

public class MemoryUseMeasurement {

    public static MemoryUseMeasurement create() {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryUseMeasurement(runtime.totalMemory() - runtime.freeMemory(), runtime.maxMemory());
    }

    private final long usedMemory;
    private final long maxMemory;

    public MemoryUseMeasurement(long usedMemory, long maxMemory) {
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

}
