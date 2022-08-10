package org.optaplanner.core.impl.partitionedsearch;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.partitionedsearch.event.PartitionedSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.partitionedsearch.queue.PartitionQueue;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionChangeMove;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionedSearchPhaseScope;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionedSearchStepScope;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.PhaseFactory;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecallerFactory;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.ChildThreadPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.OrCompositeTermination;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.thread.ThreadUtils;

/**
 * Default implementation of {@link PartitionedSearchPhase}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class DefaultPartitionedSearchPhase<Solution_> extends AbstractPhase<Solution_>
        implements PartitionedSearchPhase<Solution_>, PartitionedSearchPhaseLifecycleListener<Solution_> {

    protected final SolutionPartitioner<Solution_> solutionPartitioner;
    protected final ThreadFactory threadFactory;
    protected final Integer runnablePartThreadLimit;

    protected final List<PhaseConfig> phaseConfigList;
    protected final HeuristicConfigPolicy<Solution_> configPolicy;

    private DefaultPartitionedSearchPhase(Builder<Solution_> builder) {
        super(builder);
        solutionPartitioner = builder.solutionPartitioner;
        threadFactory = builder.threadFactory;
        runnablePartThreadLimit = builder.runnablePartThreadLimit;
        phaseConfigList = builder.phaseConfigList;
        configPolicy = builder.configPolicy;
    }

    @Override
    public String getPhaseTypeString() {
        return "Partitioned Search";
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solve(SolverScope<Solution_> solverScope) {
        PartitionedSearchPhaseScope<Solution_> phaseScope = new PartitionedSearchPhaseScope<>(solverScope);
        List<Solution_> partList = solutionPartitioner.splitWorkingSolution(
                solverScope.getScoreDirector(), runnablePartThreadLimit);
        int partCount = partList.size();
        phaseScope.setPartCount(partCount);
        phaseStarted(phaseScope);
        ExecutorService executor = createThreadPoolExecutor(partCount);
        ChildThreadPlumbingTermination<Solution_> childThreadPlumbingTermination =
                new ChildThreadPlumbingTermination<>();
        PartitionQueue<Solution_> partitionQueue = new PartitionQueue<>(partCount);
        Semaphore runnablePartThreadSemaphore = runnablePartThreadLimit == null ? null
                : new Semaphore(runnablePartThreadLimit, true);
        try {
            for (ListIterator<Solution_> it = partList.listIterator(); it.hasNext();) {
                int partIndex = it.nextIndex();
                Solution_ part = it.next();
                PartitionSolver<Solution_> partitionSolver = buildPartitionSolver(
                        childThreadPlumbingTermination, runnablePartThreadSemaphore, solverScope);
                partitionSolver.addEventListener(event -> {
                    InnerScoreDirector<Solution_, ?> childScoreDirector =
                            partitionSolver.solverScope.getScoreDirector();
                    PartitionChangeMove<Solution_> move = PartitionChangeMove.createMove(childScoreDirector, partIndex);
                    InnerScoreDirector<Solution_, ?> parentScoreDirector = solverScope.getScoreDirector();
                    move = move.rebase(parentScoreDirector);
                    partitionQueue.addMove(partIndex, move);
                });
                executor.submit(() -> {
                    try {
                        partitionSolver.solve(part);
                        long partCalculationCount = partitionSolver.getScoreCalculationCount();
                        partitionQueue.addFinish(partIndex, partCalculationCount);
                    } catch (Throwable throwable) {
                        // Any Exception or even Error that happens here (on a partition thread) must be stored
                        // in the partitionQueue in order to be propagated to the solver thread.
                        logger.trace("{}            Part thread ({}) exception that will be propagated to the solver thread.",
                                logIndentation, partIndex, throwable);
                        partitionQueue.addExceptionThrown(partIndex, throwable);
                    }
                });
            }
            for (PartitionChangeMove<Solution_> step : partitionQueue) {
                PartitionedSearchStepScope<Solution_> stepScope = new PartitionedSearchStepScope<>(phaseScope);
                stepStarted(stepScope);
                stepScope.setStep(step);
                if (logger.isDebugEnabled()) {
                    stepScope.setStepString(step.toString());
                }
                doStep(stepScope);
                stepEnded(stepScope);
                phaseScope.setLastCompletedStepScope(stepScope);
            }
            phaseScope.addChildThreadsScoreCalculationCount(partitionQueue.getPartsCalculationCount());
        } finally {
            // In case one of the partition threads threw an Exception, it is propagated here
            // but the other partition threads are not aware of the failure and may continue solving for a long time,
            // so we need to ask them to terminate. In case no exception was thrown, this does nothing.
            childThreadPlumbingTermination.terminateChildren();
            ThreadUtils.shutdownAwaitOrKill(executor, logIndentation, "Partitioned Search");
        }
        phaseEnded(phaseScope);
    }

    private ExecutorService createThreadPoolExecutor(int partCount) {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(partCount, threadFactory);
        if (threadPoolExecutor.getMaximumPoolSize() < partCount) {
            throw new IllegalStateException(
                    "The threadPoolExecutor's maximumPoolSize (" + threadPoolExecutor.getMaximumPoolSize()
                            + ") is less than the partCount (" + partCount + "), so some partitions will starve.\n"
                            + "Normally this is impossible because the threadPoolExecutor should be unbounded."
                            + " Use runnablePartThreadLimit (" + runnablePartThreadLimit
                            + ") instead to avoid CPU hogging and live locks.");
        }
        return threadPoolExecutor;
    }

    public PartitionSolver<Solution_> buildPartitionSolver(
            ChildThreadPlumbingTermination<Solution_> childThreadPlumbingTermination,
            Semaphore runnablePartThreadSemaphore, SolverScope<Solution_> solverScope) {
        BestSolutionRecaller<Solution_> bestSolutionRecaller =
                BestSolutionRecallerFactory.create().buildBestSolutionRecaller(configPolicy.getEnvironmentMode());
        Termination<Solution_> partTermination = new OrCompositeTermination<>(childThreadPlumbingTermination,
                phaseTermination.createChildThreadTermination(solverScope, ChildThreadType.PART_THREAD));
        List<Phase<Solution_>> phaseList =
                PhaseFactory.buildPhases(phaseConfigList, configPolicy, bestSolutionRecaller, partTermination);

        // TODO create PartitionSolverScope alternative to deal with 3 layer terminations
        SolverScope<Solution_> partSolverScope = solverScope.createChildThreadSolverScope(ChildThreadType.PART_THREAD);
        partSolverScope.setRunnableThreadSemaphore(runnablePartThreadSemaphore);
        return new PartitionSolver<>(bestSolutionRecaller, partTermination, phaseList, partSolverScope);
    }

    protected void doStep(PartitionedSearchStepScope<Solution_> stepScope) {
        Move<Solution_> nextStep = stepScope.getStep();
        nextStep.doMoveOnly(stepScope.getScoreDirector());
        calculateWorkingStepScore(stepScope, nextStep);
        solver.getBestSolutionRecaller().processWorkingSolutionDuringStep(stepScope);
    }

    @Override
    public void phaseStarted(PartitionedSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
    }

    @Override
    public void stepStarted(PartitionedSearchStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
    }

    @Override
    public void stepEnded(PartitionedSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        PartitionedSearchPhaseScope<Solution_> phaseScope = stepScope.getPhaseScope();
        if (logger.isDebugEnabled()) {
            logger.debug("{}    PS step ({}), time spent ({}), score ({}), {} best score ({}), picked move ({}).",
                    logIndentation,
                    stepScope.getStepIndex(),
                    phaseScope.calculateSolverTimeMillisSpentUpToNow(),
                    stepScope.getScore(),
                    (stepScope.getBestScoreImproved() ? "new" : "   "), phaseScope.getBestScore(),
                    stepScope.getStepString());
        }
    }

    @Override
    public void phaseEnded(PartitionedSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        phaseScope.endingNow();
        logger.info("{}Partitioned Search phase ({}) ended: time spent ({}), best score ({}),"
                + " score calculation speed ({}/sec), step total ({}), partCount ({}), runnablePartThreadLimit ({}).",
                logIndentation,
                phaseIndex,
                phaseScope.calculateSolverTimeMillisSpentUpToNow(),
                phaseScope.getBestScore(),
                phaseScope.getPhaseScoreCalculationSpeed(),
                phaseScope.getNextStepIndex(),
                phaseScope.getPartCount(),
                runnablePartThreadLimit);
    }

    public static class Builder<Solution_> extends AbstractPhase.Builder<Solution_> {

        private final SolutionPartitioner<Solution_> solutionPartitioner;
        private final ThreadFactory threadFactory;
        private final Integer runnablePartThreadLimit;

        private final List<PhaseConfig> phaseConfigList;
        private final HeuristicConfigPolicy<Solution_> configPolicy;

        public Builder(int phaseIndex, String logIndentation, Termination<Solution_> phaseTermination,
                SolutionPartitioner<Solution_> solutionPartitioner, ThreadFactory threadFactory,
                Integer runnablePartThreadLimit, List<PhaseConfig> phaseConfigList,
                HeuristicConfigPolicy<Solution_> configPolicy) {
            super(phaseIndex, logIndentation, phaseTermination);
            this.solutionPartitioner = solutionPartitioner;
            this.threadFactory = threadFactory;
            this.runnablePartThreadLimit = runnablePartThreadLimit;
            this.phaseConfigList = List.copyOf(phaseConfigList);
            this.configPolicy = configPolicy;
        }

        @Override
        public DefaultPartitionedSearchPhase<Solution_> build() {
            return new DefaultPartitionedSearchPhase<>(this);
        }
    }
}
