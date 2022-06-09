package org.optaplanner.benchmark.impl.statistic.memoryuse;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class MemoryUseStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final MemoryUseMeasurement memoryUseMeasurement;

    public MemoryUseStatisticPoint(long timeMillisSpent, MemoryUseMeasurement memoryUseMeasurement) {
        this.timeMillisSpent = timeMillisSpent;
        this.memoryUseMeasurement = memoryUseMeasurement;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public MemoryUseMeasurement getMemoryUseMeasurement() {
        return memoryUseMeasurement;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, memoryUseMeasurement.getUsedMemory(),
                memoryUseMeasurement.getMaxMemory());
    }

}
