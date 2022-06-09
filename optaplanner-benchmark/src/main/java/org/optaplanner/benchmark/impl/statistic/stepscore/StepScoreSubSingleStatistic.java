package org.optaplanner.benchmark.impl.statistic.stepscore;

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

public class StepScoreSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, StepScoreStatisticPoint> {

    public StepScoreSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, ProblemStatisticType.STEP_SCORE);
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void open(StatisticRegistry<Solution_> registry, Tags runTag, Solver<Solution_> solver) {
        registry.addListener(SolverMetric.STEP_SCORE,
                timeMillisSpent -> registry.extractScoreFromMeters(SolverMetric.STEP_SCORE, runTag,
                        score -> pointList.add(new StepScoreStatisticPoint(timeMillisSpent, score))));
    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return StatisticPoint.buildCsvLine("timeMillisSpent", "score");
    }

    @Override
    protected StepScoreStatisticPoint createPointFromCsvLine(ScoreDefinition<?> scoreDefinition,
            List<String> csvLine) {
        return new StepScoreStatisticPoint(Long.parseLong(csvLine.get(0)),
                scoreDefinition.parseScore(csvLine.get(1)));
    }

}
