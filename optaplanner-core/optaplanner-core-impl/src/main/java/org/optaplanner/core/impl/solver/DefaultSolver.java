package org.optaplanner.core.impl.solver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.change.ProblemChangeAdapter;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.Termination;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

/**
 * Default implementation for {@link Solver}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Solver
 * @see AbstractSolver
 */
public class DefaultSolver<Solution_> extends AbstractSolver<Solution_> {

    protected EnvironmentMode environmentMode;
    protected RandomFactory randomFactory;

    protected BasicPlumbingTermination<Solution_> basicPlumbingTermination;

    protected final AtomicBoolean solving = new AtomicBoolean(false);

    protected final SolverScope<Solution_> solverScope;

    private final String moveThreadCountDescription;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public DefaultSolver(EnvironmentMode environmentMode, RandomFactory randomFactory,
            BestSolutionRecaller<Solution_> bestSolutionRecaller,
            BasicPlumbingTermination<Solution_> basicPlumbingTermination, Termination<Solution_> termination,
            List<Phase<Solution_>> phaseList, SolverScope<Solution_> solverScope, String moveThreadCountDescription) {
        super(bestSolutionRecaller, termination, phaseList);
        this.environmentMode = environmentMode;
        this.randomFactory = randomFactory;
        this.basicPlumbingTermination = basicPlumbingTermination;
        this.solverScope = solverScope;
        this.moveThreadCountDescription = moveThreadCountDescription;
    }

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public RandomFactory getRandomFactory() {
        return randomFactory;
    }

    public InnerScoreDirectorFactory<Solution_, ?> getScoreDirectorFactory() {
        return solverScope.getScoreDirector().getScoreDirectorFactory();
    }

    public SolverScope<Solution_> getSolverScope() {
        return solverScope;
    }

    // ************************************************************************
    // Complex getters
    // ************************************************************************

    public long getTimeMillisSpent() {
        Long startingSystemTimeMillis = solverScope.getStartingSystemTimeMillis();
        if (startingSystemTimeMillis == null) {
            // The solver hasn't started yet
            return 0L;
        }
        Long endingSystemTimeMillis = solverScope.getEndingSystemTimeMillis();
        if (endingSystemTimeMillis == null) {
            // The solver hasn't ended yet
            endingSystemTimeMillis = System.currentTimeMillis();
        }
        return endingSystemTimeMillis - startingSystemTimeMillis;
    }

    @Override
    public boolean isSolving() {
        return solving.get();
    }

    @Override
    public boolean terminateEarly() {
        boolean terminationEarlySuccessful = basicPlumbingTermination.terminateEarly();
        if (terminationEarlySuccessful) {
            logger.info("Terminating solver early.");
        }
        return terminationEarlySuccessful;
    }

    @Override
    public boolean isTerminateEarly() {
        return basicPlumbingTermination.isTerminateEarly();
    }

    @Override
    public boolean addProblemFactChange(ProblemFactChange<Solution_> problemFactChange) {
        return basicPlumbingTermination.addProblemChange(ProblemChangeAdapter.create(problemFactChange));
    }

    @Override
    public boolean addProblemFactChanges(List<ProblemFactChange<Solution_>> problemFactChangeList) {
        Objects.requireNonNull(problemFactChangeList,
                () -> "The list of problem fact changes (" + problemFactChangeList + ") cannot be null.");
        List<ProblemChangeAdapter<Solution_>> problemChangeAdapterList = problemFactChangeList.stream()
                .map(ProblemChangeAdapter::create)
                .collect(Collectors.toList());
        return basicPlumbingTermination.addProblemChanges(problemChangeAdapterList);
    }

    @Override
    public void addProblemChange(ProblemChange<Solution_> problemChange) {
        basicPlumbingTermination.addProblemChange(ProblemChangeAdapter.create(problemChange));
    }

    @Override
    public void addProblemChanges(List<ProblemChange<Solution_>> problemChangeList) {
        Objects.requireNonNull(problemChangeList,
                () -> "The list of problem changes (" + problemChangeList + ") cannot be null.");
        problemChangeList.forEach(this::addProblemChange);
    }

    @Override
    public boolean isEveryProblemChangeProcessed() {
        return basicPlumbingTermination.isEveryProblemFactChangeProcessed();
    }

    @Override
    public boolean isEveryProblemFactChangeProcessed() {
        return basicPlumbingTermination.isEveryProblemFactChangeProcessed();
    }

    public void setMonitorTagMap(Map<String, String> monitorTagMap) {
        Tags monitoringTags = Objects.requireNonNullElse(monitorTagMap, Collections.<String, String> emptyMap())
                .entrySet().stream().map(entry -> Tags.of(entry.getKey(), entry.getValue()))
                .reduce(Tags.empty(), Tags::and);
        solverScope.setMonitoringTags(monitoringTags);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public final Solution_ solve(Solution_ problem) {
        if (problem == null) {
            throw new IllegalArgumentException("The problem (" + problem + ") must not be null.");
        }

        // No tags for these metrics; they are global
        LongTaskTimer solveLengthTimer = Metrics.more().longTaskTimer(SolverMetric.SOLVE_DURATION.getMeterId());
        Counter errorCounter = Metrics.counter(SolverMetric.ERROR_COUNT.getMeterId());

        // Score Calculation Count is specific per solver
        Metrics.gauge(SolverMetric.SCORE_CALCULATION_COUNT.getMeterId(), solverScope.getMonitoringTags(),
                solverScope, SolverScope::getScoreCalculationCount);
        solverScope.getSolverMetricSet().forEach(solverMetric -> solverMetric.register(this));

        solverScope.setBestSolution(problem);
        outerSolvingStarted(solverScope);
        boolean restartSolver = true;
        while (restartSolver) {
            LongTaskTimer.Sample sample = solveLengthTimer.start();
            try {
                solvingStarted(solverScope);
                runPhases(solverScope);
                solvingEnded(solverScope);
            } catch (Exception e) {
                errorCounter.increment();
                throw e;
            } finally {
                sample.stop();
                Metrics.globalRegistry.remove(new Meter.Id(SolverMetric.SCORE_CALCULATION_COUNT.getMeterId(),
                        solverScope.getMonitoringTags(),
                        null,
                        null,
                        Meter.Type.GAUGE));
                solverScope.getSolverMetricSet().forEach(solverMetric -> solverMetric.unregister(this));
            }
            restartSolver = checkProblemFactChanges();
        }
        outerSolvingEnded(solverScope);
        return solverScope.getBestSolution();
    }

    public void outerSolvingStarted(SolverScope<Solution_> solverScope) {
        solving.set(true);
        basicPlumbingTermination.resetTerminateEarly();
        solverScope.setStartingSolverCount(0);
        solverScope.setWorkingRandom(randomFactory.createRandom());
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        solverScope.startingNow();
        solverScope.getScoreDirector().resetCalculationCount();
        super.solvingStarted(solverScope);
        int startingSolverCount = solverScope.getStartingSolverCount() + 1;
        solverScope.setStartingSolverCount(startingSolverCount);
        logger.info("Solving {}: time spent ({}), best score ({}), environment mode ({}), "
                + "move thread count ({}), random ({}).",
                (startingSolverCount == 1 ? "started" : "restarted"),
                solverScope.calculateTimeMillisSpentUpToNow(),
                solverScope.getBestScore(),
                environmentMode.name(),
                moveThreadCountDescription,
                (randomFactory != null ? randomFactory : "not fixed"));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        solverScope.endingNow();
    }

    public void outerSolvingEnded(SolverScope<Solution_> solverScope) {
        // Must be kept open for doProblemFactChange
        solverScope.getScoreDirector().close();
        logger.info("Solving ended: time spent ({}), best score ({}), score calculation speed ({}/sec), "
                + "phase total ({}), environment mode ({}), move thread count ({}).",
                solverScope.getTimeMillisSpent(),
                solverScope.getBestScore(),
                solverScope.getScoreCalculationSpeed(),
                phaseList.size(),
                environmentMode.name(),
                moveThreadCountDescription);
        solving.set(false);
    }

    private boolean checkProblemFactChanges() {
        boolean restartSolver = basicPlumbingTermination.waitForRestartSolverDecision();
        if (!restartSolver) {
            return false;
        } else {
            BlockingQueue<ProblemChangeAdapter<Solution_>> problemFactChangeQueue = basicPlumbingTermination
                    .startProblemFactChangesProcessing();
            solverScope.setWorkingSolutionFromBestSolution();

            int stepIndex = 0;
            ProblemChangeAdapter<Solution_> problemChangeAdapter = problemFactChangeQueue.poll();
            while (problemChangeAdapter != null) {
                problemChangeAdapter.doProblemChange(solverScope);
                logger.debug("    Real-time problem change applied; step index ({}).", stepIndex);
                stepIndex++;
                problemChangeAdapter = problemFactChangeQueue.poll();
            }
            // All PFCs are processed, fail fast if any of the new facts have null planning IDs.
            InnerScoreDirector<Solution_, ?> scoreDirector = solverScope.getScoreDirector();
            scoreDirector.assertNonNullPlanningIds();
            // Everything is fine, proceed.
            Score<?> score = scoreDirector.calculateScore();
            basicPlumbingTermination.endProblemFactChangesProcessing();
            bestSolutionRecaller.updateBestSolutionWithoutFiring(solverScope);
            logger.info("Real-time problem fact changes done: step total ({}), new best score ({}).",
                    stepIndex, score);
            return true;
        }
    }
}
