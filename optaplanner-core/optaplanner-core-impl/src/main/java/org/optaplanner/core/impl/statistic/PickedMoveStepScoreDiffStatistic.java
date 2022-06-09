package org.optaplanner.core.impl.statistic;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolver;

import io.micrometer.core.instrument.Tags;

public class PickedMoveStepScoreDiffStatistic<Solution_> implements SolverStatistic<Solution_> {

    private final Map<Solver<Solution_>, PhaseLifecycleListenerAdapter<Solution_>> solverToPhaseLifecycleListenerMap =
            new WeakHashMap<>();

    @Override
    public void unregister(Solver<Solution_> solver) {
        PhaseLifecycleListenerAdapter<Solution_> listener = solverToPhaseLifecycleListenerMap.remove(solver);
        if (listener != null) {
            ((DefaultSolver<Solution_>) solver).removePhaseLifecycleListener(listener);
        }
    }

    @Override
    public void register(Solver<Solution_> solver) {
        DefaultSolver<Solution_> defaultSolver = (DefaultSolver<Solution_>) solver;
        InnerScoreDirectorFactory<Solution_, ?> innerScoreDirectorFactory = defaultSolver.getScoreDirectorFactory();
        SolutionDescriptor<Solution_> solutionDescriptor = innerScoreDirectorFactory.getSolutionDescriptor();
        PickedMoveStepScoreDiffStatisticListener<Solution_, ?> listener =
                new PickedMoveStepScoreDiffStatisticListener<>((ScoreDefinition<?>) solutionDescriptor.getScoreDefinition());
        solverToPhaseLifecycleListenerMap.put(solver, listener);
        defaultSolver.addPhaseLifecycleListener(listener);
    }

    private static class PickedMoveStepScoreDiffStatisticListener<Solution_, Score_ extends Score<Score_>>
            extends PhaseLifecycleListenerAdapter<Solution_> {
        private Score_ oldStepScore = null;
        private final ScoreDefinition<Score_> scoreDefinition;
        private final Map<Tags, List<AtomicReference<Number>>> tagsToMoveScoreMap = new ConcurrentHashMap<>();

        public PickedMoveStepScoreDiffStatisticListener(ScoreDefinition<Score_> scoreDefinition) {
            this.scoreDefinition = scoreDefinition;
        }

        @Override
        public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldStepScore = phaseScope.getStartingScore();
            }
        }

        @Override
        public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldStepScore = null;
            }
        }

        @Override
        public void stepEnded(AbstractStepScope<Solution_> stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope<Solution_>) stepScope);
            }
        }

        @SuppressWarnings("unchecked")
        private void localSearchStepEnded(LocalSearchStepScope<Solution_> stepScope) {
            String moveType = stepScope.getStep().getSimpleMoveTypeDescription();
            Score_ newStepScore = (Score_) stepScope.getScore();
            Score_ stepScoreDiff = newStepScore.subtract(oldStepScore);
            oldStepScore = newStepScore;

            SolverMetric.registerScoreMetrics(SolverMetric.PICKED_MOVE_TYPE_STEP_SCORE_DIFF,
                    stepScope.getPhaseScope().getSolverScope().getMonitoringTags()
                            .and("move.type", moveType),
                    scoreDefinition,
                    tagsToMoveScoreMap,
                    stepScoreDiff);
        }
    }
}
