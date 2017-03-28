/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.partitionedsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.recaller.BestSolutionRecallerConfig;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.partitionedsearch.event.PartitionedSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.partitionedsearch.queue.PartitionQueue;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionChangeMove;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionedSearchPhaseScope;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionedSearchStepScope;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.ChildThreadType;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.ChildThreadPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.OrCompositeTermination;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * Default implementation of {@link PartitionedSearchPhase}.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class DefaultPartitionedSearchPhase<Solution_> extends AbstractPhase<Solution_>
        implements PartitionedSearchPhase<Solution_>, PartitionedSearchPhaseLifecycleListener<Solution_> {

    protected SolutionPartitioner<Solution_> solutionPartitioner;
    protected ThreadFactory threadFactory;
    protected Integer runnablePartThreadLimit;

    protected List<PhaseConfig> phaseConfigList;
    protected HeuristicConfigPolicy configPolicy;

    public DefaultPartitionedSearchPhase(int phaseIndex, String logIndentation,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination termination) {
        super(phaseIndex, logIndentation, bestSolutionRecaller, termination);
    }

    public void setSolutionPartitioner(SolutionPartitioner<Solution_> solutionPartitioner) {
        this.solutionPartitioner = solutionPartitioner;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void setRunnablePartThreadLimit(Integer runnablePartThreadLimit) {
        this.runnablePartThreadLimit = runnablePartThreadLimit;
    }

    public void setPhaseConfigList(List<PhaseConfig> phaseConfigList) {
        this.phaseConfigList = phaseConfigList;
    }

    public void setConfigPolicy(HeuristicConfigPolicy configPolicy) {
        this.configPolicy = configPolicy;
    }

    @Override
    public String getPhaseTypeString() {
        return "Partitioned Search";
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solve(DefaultSolverScope<Solution_> solverScope) {
        PartitionedSearchPhaseScope<Solution_> phaseScope = new PartitionedSearchPhaseScope<>(solverScope);
        List<Solution_> partList = solutionPartitioner.splitWorkingSolution(
                solverScope.getScoreDirector(), runnablePartThreadLimit);
        int partCount = partList.size();
        phaseScope.setPartCount(partCount);
        phaseStarted(phaseScope);
        ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor();
        if (threadPoolExecutor.getMaximumPoolSize() < partCount) {
            throw new IllegalStateException(
                    "The threadPoolExecutor's maximumPoolSize (" + threadPoolExecutor.getMaximumPoolSize()
                    + ") is less than the partCount (" + partCount + "), so some partitions will starve.\n"
                    + "Normally this impossible because the threadPoolExecutor should be unbounded:"
                    + " Use runnablePartThreadLimit (" + runnablePartThreadLimit
                    + ") instead to avoid CPU hogging and live locks.");
        }
        ChildThreadPlumbingTermination childThreadPlumbingTermination = new ChildThreadPlumbingTermination();
        PartitionQueue<Solution_> partitionQueue = new PartitionQueue<>(partCount);
        Semaphore runnablePartThreadSemaphore
                = runnablePartThreadLimit == null ? null : new Semaphore(runnablePartThreadLimit);
        try {
            for (ListIterator<Solution_> it = partList.listIterator(); it.hasNext();) {
                int partIndex = it.nextIndex();
                Solution_ part = it.next();
                PartitionSolver<Solution_> partitionSolver = buildPartitionSolver(
                        childThreadPlumbingTermination, runnablePartThreadSemaphore, solverScope);
                partitionSolver.addEventListener(event -> {
                    InnerScoreDirector<Solution_> childScoreDirector = partitionSolver.solverScope.getScoreDirector();
                    PartitionChangeMove<Solution_> move = PartitionChangeMove.createMove(childScoreDirector);
                    InnerScoreDirector<Solution_> parentScoreDirector = solverScope.getScoreDirector();
                    move = move.rebase(parentScoreDirector);
                    partitionQueue.addMove(partIndex, move);
                });
                threadPoolExecutor.submit(() -> {
                    try {
                        partitionSolver.solve(part);
                        partitionQueue.addFinish(partIndex);
                    } catch (Throwable throwable) {
                        // Any Exception or even Error that happens here (on a partition thread) must be stored
                        // in the partitionQueue in order to be propagated to the solver thread
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
        } finally {
            // If a partition thread throws an Exception, it is propagated here
            // but the other partition threads won't finish any time soon, so we need to ask them to terminate
            childThreadPlumbingTermination.terminateChildren();
            try {
                // First wait for solvers to terminate voluntarily (because we have just issued children termination)
                if (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                    // Some solvers refused to complete. Busy threads will be interrupted in the finally block
                    logger.warn("{}Partitioned Search threadPoolExecutor didn't terminate within timeout (1 second).",
                            logIndentation);
                }
            } catch (InterruptedException e) {
                // Interrupted while waiting for thread pool termination. Busy threads will be interrupted
                // in the finally block
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread pool shutdown was interrupted.", e);
            } finally {
                threadPoolExecutor.shutdownNow();
            }
        }
        phaseEnded(phaseScope);
    }

    private ThreadPoolExecutor createThreadPoolExecutor() {
        // Based on Executors.newCachedThreadPool(...)
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                threadFactory);
    }

    public PartitionSolver<Solution_> buildPartitionSolver(
            ChildThreadPlumbingTermination childThreadPlumbingTermination, Semaphore runnablePartThreadSemaphore,
            DefaultSolverScope<Solution_> solverScope) {
        Termination partTermination = new OrCompositeTermination(childThreadPlumbingTermination,
                termination.createChildThreadTermination(solverScope, ChildThreadType.PART_THREAD));
        BestSolutionRecaller<Solution_> bestSolutionRecaller = new BestSolutionRecallerConfig()
                .buildBestSolutionRecaller(configPolicy.getEnvironmentMode());
        List<Phase<Solution_>> phaseList = new ArrayList<>(phaseConfigList.size());
        int partPhaseIndex = 0;
        for (PhaseConfig phaseConfig : phaseConfigList) {
            phaseList.add(phaseConfig.buildPhase(partPhaseIndex, configPolicy, bestSolutionRecaller, partTermination));
            partPhaseIndex++;
        }
        // TODO create PartitionSolverScope alternative to deal with 3 layer terminations
        DefaultSolverScope<Solution_> partSolverScope
                = solverScope.createChildThreadSolverScope(ChildThreadType.PART_THREAD);
        partSolverScope.setRunnableThreadSemaphore(runnablePartThreadSemaphore);
        return new PartitionSolver<>(partTermination, bestSolutionRecaller, phaseList, partSolverScope);
    }

    protected void doStep(PartitionedSearchStepScope<Solution_> stepScope) {
        Move<Solution_> nextStep = stepScope.getStep();
        nextStep.doMove(stepScope.getScoreDirector());
        calculateWorkingStepScore(stepScope, nextStep);
        bestSolutionRecaller.processWorkingSolutionDuringStep(stepScope);
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

}
