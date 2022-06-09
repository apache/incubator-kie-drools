package org.optaplanner.benchmark.impl.statistic.bestsolutionmutation;

import java.util.List;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemBasedSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.benchmark.impl.statistic.StatisticRegistry;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

import io.micrometer.core.instrument.Tags;

public class BestSolutionMutationSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, BestSolutionMutationStatisticPoint> {

    public BestSolutionMutationSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, ProblemStatisticType.BEST_SOLUTION_MUTATION);
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void open(StatisticRegistry<Solution_> registry, Tags runTag, Solver<Solution_> solver) {
        registry.addListener(SolverMetric.BEST_SOLUTION_MUTATION,
                timestamp -> registry.getGaugeValue(SolverMetric.BEST_SOLUTION_MUTATION, runTag,
                        mutationCount -> pointList
                                .add(new BestSolutionMutationStatisticPoint(timestamp, mutationCount.intValue()))));
    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return StatisticPoint.buildCsvLine("timeMillisSpent", "mutationCount");
    }

    @Override
    protected BestSolutionMutationStatisticPoint createPointFromCsvLine(ScoreDefinition<?> scoreDefinition,
            List<String> csvLine) {
        return new BestSolutionMutationStatisticPoint(Long.parseLong(csvLine.get(0)),
                Integer.parseInt(csvLine.get(1)));
    }

}
