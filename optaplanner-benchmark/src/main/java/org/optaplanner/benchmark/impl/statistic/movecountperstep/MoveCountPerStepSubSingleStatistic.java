package org.optaplanner.benchmark.impl.statistic.movecountperstep;

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

public class MoveCountPerStepSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, MoveCountPerStepStatisticPoint> {

    public MoveCountPerStepSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, ProblemStatisticType.MOVE_COUNT_PER_STEP);
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void open(StatisticRegistry<Solution_> registry, Tags runTag, Solver<Solution_> solver) {
        registry.addListener(SolverMetric.MOVE_COUNT_PER_STEP,
                timeMillisSpent -> registry.getGaugeValue(SolverMetric.MOVE_COUNT_PER_STEP.getMeterId() + ".accepted", runTag,
                        accepted -> registry.getGaugeValue(SolverMetric.MOVE_COUNT_PER_STEP.getMeterId() + ".selected", runTag,
                                selected -> pointList.add(new MoveCountPerStepStatisticPoint(timeMillisSpent,
                                        new MoveCountPerStepMeasurement(accepted.longValue(), selected.longValue()))))));
    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return StatisticPoint.buildCsvLine("timeMillisSpent", "acceptedMoveCount", "selectedMoveCount");
    }

    @Override
    protected MoveCountPerStepStatisticPoint createPointFromCsvLine(ScoreDefinition<?> scoreDefinition,
            List<String> csvLine) {
        return new MoveCountPerStepStatisticPoint(Long.parseLong(csvLine.get(0)),
                new MoveCountPerStepMeasurement(Long.parseLong(csvLine.get(1)), Long.parseLong(csvLine.get(2))));
    }

}
