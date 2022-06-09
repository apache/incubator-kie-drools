package org.optaplanner.benchmark.impl.statistic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.util.Pair;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class StatisticRegistry<Solution_> extends SimpleMeterRegistry
        implements PhaseLifecycleListener<Solution_> {

    List<BiConsumer<Long, AbstractStepScope<Solution_>>> stepMeterListenerList = new ArrayList<>();
    List<BiConsumer<Long, AbstractStepScope<Solution_>>> bestSolutionMeterListenerList = new ArrayList<>();
    AbstractStepScope<Solution_> bestSolutionStepScope = null;
    long bestSolutionChangedTimestamp = Long.MIN_VALUE;
    boolean lastStepImprovedSolution = false;
    ScoreDefinition<?> scoreDefinition;
    final Function<Number, Number> scoreLevelNumberConverter;

    public StatisticRegistry(DefaultSolver<Solution_> solver) {
        scoreDefinition = solver.getSolverScope().getScoreDefinition();
        Number zeroScoreLevel0 = scoreDefinition.getZeroScore().toLevelNumbers()[0];
        if (zeroScoreLevel0 instanceof BigDecimal) {
            scoreLevelNumberConverter = number -> BigDecimal.valueOf(number.doubleValue());
        } else if (zeroScoreLevel0 instanceof BigInteger) {
            scoreLevelNumberConverter = number -> BigInteger.valueOf(number.longValue());
        } else if (zeroScoreLevel0 instanceof Double) {
            scoreLevelNumberConverter = Number::doubleValue;
        } else if (zeroScoreLevel0 instanceof Float) {
            scoreLevelNumberConverter = Number::floatValue;
        } else if (zeroScoreLevel0 instanceof Long) {
            scoreLevelNumberConverter = Number::longValue;
        } else if (zeroScoreLevel0 instanceof Integer) {
            scoreLevelNumberConverter = Number::intValue;
        } else if (zeroScoreLevel0 instanceof Short) {
            scoreLevelNumberConverter = Number::shortValue;
        } else if (zeroScoreLevel0 instanceof Byte) {
            scoreLevelNumberConverter = Number::byteValue;
        } else {
            throw new IllegalStateException(
                    "Cannot determine score level type for score definition (" + scoreDefinition.getClass().getName() + ").");
        }
    }

    public void addListener(SolverMetric metric, Consumer<Long> listener) {
        addListener(metric, (timestamp, stepScope) -> listener.accept(timestamp));
    }

    public void addListener(SolverMetric metric, BiConsumer<Long, AbstractStepScope<Solution_>> listener) {
        if (metric.isMetricBestSolutionBased()) {
            bestSolutionMeterListenerList.add(listener);
        } else {
            stepMeterListenerList.add(listener);
        }
    }

    public Set<Meter.Id> getMeterIds(SolverMetric metric, Tags runId) {
        return Search.in(this).name(name -> name.startsWith(metric.getMeterId())).tags(runId)
                .meters().stream().map(Meter::getId)
                .collect(Collectors.toSet());
    }

    public void extractScoreFromMeters(SolverMetric metric, Tags runId, Consumer<Score<?>> scoreConsumer) {
        String[] labelNames = scoreDefinition.getLevelLabels();
        for (int i = 0; i < labelNames.length; i++) {
            labelNames[i] = labelNames[i].replace(' ', '.');
        }
        Number[] levelNumbers = new Number[labelNames.length];
        for (int i = 0; i < labelNames.length; i++) {
            Gauge scoreLevelGauge = this.find(metric.getMeterId() + "." + labelNames[i]).tags(runId).gauge();
            if (scoreLevelGauge != null && Double.isFinite(scoreLevelGauge.value())) {
                levelNumbers[i] = scoreLevelNumberConverter.apply(scoreLevelGauge.value());
            } else {
                return;
            }
        }
        scoreConsumer.accept(scoreDefinition.fromLevelNumbers(0, levelNumbers));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void extractConstraintSummariesFromMeters(SolverMetric metric, Tags runId,
            Consumer<ConstraintSummary<?>> constraintMatchTotalConsumer) {
        Set<Meter.Id> meterIds = getMeterIds(metric, runId);
        Set<Pair<String, String>> constraintPackageNamePairs = new HashSet<>();
        // Add the constraint ids from the meter ids
        meterIds.forEach(meterId -> constraintPackageNamePairs
                .add(Pair.of(meterId.getTag("constraint.package"), meterId.getTag("constraint.name"))));
        constraintPackageNamePairs.forEach(constraintPackageNamePair -> {
            String constraintPackage = constraintPackageNamePair.getKey();
            String constraintName = constraintPackageNamePair.getValue();
            Tags constraintMatchTotalRunId = runId.and("constraint.package", constraintPackage)
                    .and("constraint.name", constraintName);
            // Get the score from the corresponding constraint package and constraint name meters
            extractScoreFromMeters(metric, constraintMatchTotalRunId,
                    // Get the count gauge (add constraint package and constraint name to the run tags)
                    score -> getGaugeValue(metric.getMeterId() + ".count",
                            constraintMatchTotalRunId,
                            count -> constraintMatchTotalConsumer.accept(
                                    new ConstraintSummary(constraintPackage, constraintName, score, count.intValue()))));
        });
    }

    public void getGaugeValue(SolverMetric metric, Tags runId, Consumer<Number> gaugeConsumer) {
        getGaugeValue(metric.getMeterId(), runId, gaugeConsumer);
    }

    public void getGaugeValue(String meterId, Tags runId, Consumer<Number> gaugeConsumer) {
        Gauge gauge = this.find(meterId).tags(runId).gauge();
        if (gauge != null && Double.isFinite(gauge.value())) {
            gaugeConsumer.accept(gauge.value());
        }
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        final long timestamp =
                System.currentTimeMillis() - stepScope.getPhaseScope().getSolverScope().getStartingSystemTimeMillis();
        stepMeterListenerList.forEach(listener -> listener.accept(timestamp, stepScope));
        if (stepScope.getBestScoreImproved()) {
            // Since best solution metrics are updated in a best solution listener, we need
            // to delay updating it until after the best solution listeners were processed
            bestSolutionStepScope = stepScope;
            bestSolutionChangedTimestamp = timestamp;
            lastStepImprovedSolution = true;
        }
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        // intentional empty
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        if (lastStepImprovedSolution) {
            bestSolutionMeterListenerList
                    .forEach(listener -> listener.accept(bestSolutionChangedTimestamp, bestSolutionStepScope));
            lastStepImprovedSolution = false;
        }
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        // intentional empty
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        // intentional empty
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        if (lastStepImprovedSolution) {
            bestSolutionMeterListenerList
                    .forEach(listener -> listener.accept(bestSolutionChangedTimestamp, bestSolutionStepScope));
            lastStepImprovedSolution = false;
        }
    }
}
