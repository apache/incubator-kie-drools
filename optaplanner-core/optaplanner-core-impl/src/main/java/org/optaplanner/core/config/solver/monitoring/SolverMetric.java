package org.optaplanner.core.config.solver.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.statistic.BestScoreStatistic;
import org.optaplanner.core.impl.statistic.BestSolutionMutationCountStatistic;
import org.optaplanner.core.impl.statistic.MemoryUseStatistic;
import org.optaplanner.core.impl.statistic.PickedMoveBestScoreDiffStatistic;
import org.optaplanner.core.impl.statistic.PickedMoveStepScoreDiffStatistic;
import org.optaplanner.core.impl.statistic.SolverStatistic;
import org.optaplanner.core.impl.statistic.StatelessSolverStatistic;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

@XmlEnum
public enum SolverMetric {
    SOLVE_DURATION("optaplanner.solver.solve.duration", false),
    ERROR_COUNT("optaplanner.solver.errors", false),
    BEST_SCORE("optaplanner.solver.best.score", new BestScoreStatistic<>(), true),
    STEP_SCORE("optaplanner.solver.step.score", false),
    SCORE_CALCULATION_COUNT("optaplanner.solver.score.calculation.count", false),
    BEST_SOLUTION_MUTATION("optaplanner.solver.best.solution.mutation", new BestSolutionMutationCountStatistic<>(), true),
    MOVE_COUNT_PER_STEP("optaplanner.solver.step.move.count", false),
    MEMORY_USE("jvm.memory.used", new MemoryUseStatistic<>(), false),
    CONSTRAINT_MATCH_TOTAL_BEST_SCORE("optaplanner.solver.constraint.match.best.score", true),
    CONSTRAINT_MATCH_TOTAL_STEP_SCORE("optaplanner.solver.constraint.match.step.score", false),
    PICKED_MOVE_TYPE_BEST_SCORE_DIFF("optaplanner.solver.move.type.best.score.diff", new PickedMoveBestScoreDiffStatistic<>(),
            true),
    PICKED_MOVE_TYPE_STEP_SCORE_DIFF("optaplanner.solver.move.type.step.score.diff", new PickedMoveStepScoreDiffStatistic<>(),
            false);

    String meterId;
    @SuppressWarnings("rawtypes")
    SolverStatistic registerFunction;
    boolean isBestSolutionBased;

    SolverMetric(String meterId, boolean isBestSolutionBased) {
        this(meterId, new StatelessSolverStatistic<>(), isBestSolutionBased);
    }

    SolverMetric(String meterId, SolverStatistic<?> registerFunction, boolean isBestSolutionBased) {
        this.meterId = meterId;
        this.registerFunction = registerFunction;
        this.isBestSolutionBased = isBestSolutionBased;
    }

    public String getMeterId() {
        return meterId;
    }

    public static void registerScoreMetrics(SolverMetric metric, Tags tags, ScoreDefinition<?> scoreDefinition,
            Map<Tags, List<AtomicReference<Number>>> tagToScoreLevels, Score<?> score) {
        Number[] levelValues = score.toLevelNumbers();
        if (tagToScoreLevels.containsKey(tags)) {
            List<AtomicReference<Number>> scoreLevels = tagToScoreLevels.get(tags);
            for (int i = 0; i < levelValues.length; i++) {
                scoreLevels.get(i).set(levelValues[i]);
            }
        } else {
            String[] levelLabels = scoreDefinition.getLevelLabels();
            for (int i = 0; i < levelLabels.length; i++) {
                levelLabels[i] = levelLabels[i].replace(' ', '.');
            }
            List<AtomicReference<Number>> scoreLevels = new ArrayList<>(levelValues.length);
            for (int i = 0; i < levelValues.length; i++) {
                scoreLevels.add(Metrics.gauge(metric.getMeterId() + "." + levelLabels[i],
                        tags, new AtomicReference<>(levelValues[i]),
                        ar -> ar.get().doubleValue()));
            }
            tagToScoreLevels.put(tags, scoreLevels);
        }
    }

    public boolean isMetricBestSolutionBased() {
        return isBestSolutionBased;
    }

    @SuppressWarnings("unchecked")
    public void register(Solver<?> solver) {
        registerFunction.register(solver);
    }

    @SuppressWarnings("unchecked")
    public void unregister(Solver<?> solver) {
        registerFunction.unregister(solver);
    }
}
