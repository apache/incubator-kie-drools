package org.optaplanner.benchmark.impl.statistic;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;

public abstract class ProblemBasedSubSingleStatistic<Solution_, StatisticPoint_ extends StatisticPoint>
        extends SubSingleStatistic<Solution_, StatisticPoint_> {

    protected final ProblemStatisticType problemStatisticType;

    protected ProblemBasedSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult,
            ProblemStatisticType problemStatisticType) {
        super(subSingleBenchmarkResult);
        this.problemStatisticType = problemStatisticType;
    }

    @Override
    public ProblemStatisticType getStatisticType() {
        return problemStatisticType;
    }

    @Override
    public String toString() {
        return subSingleBenchmarkResult + "_" + problemStatisticType;
    }

}
