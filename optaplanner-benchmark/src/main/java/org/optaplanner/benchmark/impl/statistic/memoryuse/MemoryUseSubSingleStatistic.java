package org.optaplanner.benchmark.impl.statistic.memoryuse;

import java.util.List;
import java.util.function.Consumer;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemBasedSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.benchmark.impl.statistic.StatisticRegistry;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

import io.micrometer.core.instrument.Tags;

public class MemoryUseSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, MemoryUseStatisticPoint> {

    private final long timeMillisThresholdInterval;

    public MemoryUseSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        this(subSingleBenchmarkResult, 1000L);
    }

    public MemoryUseSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult, long timeMillisThresholdInterval) {
        super(subSingleBenchmarkResult, ProblemStatisticType.MEMORY_USE);
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void open(StatisticRegistry<Solution_> registry, Tags runTag, Solver<Solution_> solver) {
        registry.addListener(SolverMetric.MEMORY_USE, new MemoryUseSubSingleStatisticListener(registry, runTag));
    }

    private class MemoryUseSubSingleStatisticListener implements Consumer<Long> {

        private long nextTimeMillisThreshold = timeMillisThresholdInterval;
        private final StatisticRegistry<?> registry;
        private final Tags tags;

        public MemoryUseSubSingleStatisticListener(StatisticRegistry<?> registry, Tags tags) {
            this.registry = registry;
            this.tags = tags;
        }

        @Override
        public void accept(Long timeMillisSpent) {
            if (timeMillisSpent >= nextTimeMillisThreshold) {
                registry.getGaugeValue(SolverMetric.MEMORY_USE, tags,
                        memoryUse -> pointList.add(
                                new MemoryUseStatisticPoint(timeMillisSpent, new MemoryUseMeasurement(memoryUse.longValue(),
                                        (long) registry.find("jvm.memory.max").tags(tags).gauge().value()))));

                nextTimeMillisThreshold += timeMillisThresholdInterval;
                if (nextTimeMillisThreshold < timeMillisSpent) {
                    nextTimeMillisThreshold = timeMillisSpent;
                }
            }
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return StatisticPoint.buildCsvLine("timeMillisSpent", "usedMemory", "maxMemory");
    }

    @Override
    protected MemoryUseStatisticPoint createPointFromCsvLine(ScoreDefinition<?> scoreDefinition,
            List<String> csvLine) {
        return new MemoryUseStatisticPoint(Long.parseLong(csvLine.get(0)),
                new MemoryUseMeasurement(Long.parseLong(csvLine.get(1)), Long.parseLong(csvLine.get(2))));
    }

}
