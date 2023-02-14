package org.optaplanner.benchmark.impl.statistic.memoryuse;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class MemoryUseStatisticPoint extends StatisticPoint {

    public static MemoryUseStatisticPoint create(long timeMillisSpent) {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryUseStatisticPoint(timeMillisSpent, runtime.totalMemory() - runtime.freeMemory(), runtime.maxMemory());
    }

    private final long timeMillisSpent;
    private final long usedMemory;
    private final long maxMemory;

    public MemoryUseStatisticPoint(long timeMillisSpent, long usedMemory, long maxMemory) {
        this.timeMillisSpent = timeMillisSpent;
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, usedMemory, maxMemory);
    }

}
