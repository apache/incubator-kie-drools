/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.constructionheuristic.decider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForager;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.thread.ApplyStepOperation;
import org.optaplanner.core.impl.heuristic.thread.DestroyOperation;
import org.optaplanner.core.impl.heuristic.thread.MoveEvaluationOperation;
import org.optaplanner.core.impl.heuristic.thread.MoveThreadOperation;
import org.optaplanner.core.impl.heuristic.thread.MoveThreadRunner;
import org.optaplanner.core.impl.heuristic.thread.OrderByMoveIndexBlockingQueue;
import org.optaplanner.core.impl.heuristic.thread.SetupOperation;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.thread.ThreadUtils;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class MultiThreadedConstructionHeuristicDecider<Solution_> extends ConstructionHeuristicDecider<Solution_> {

    protected final ThreadFactory threadFactory;
    protected final int moveThreadCount;
    protected final int selectedMoveBufferSize;

    protected boolean assertStepScoreFromScratch = false;
    protected boolean assertExpectedStepScore = false;
    protected boolean assertShadowVariablesAreNotStaleAfterStep = false;

    protected BlockingQueue<MoveThreadOperation<Solution_>> operationQueue;
    protected OrderByMoveIndexBlockingQueue<Solution_> resultQueue;
    protected CyclicBarrier moveThreadBarrier;
    protected ExecutorService executor;
    protected List<MoveThreadRunner<Solution_, ?>> moveThreadRunnerList;

    public MultiThreadedConstructionHeuristicDecider(String logIndentation, Termination<Solution_> termination,
            ConstructionHeuristicForager<Solution_> forager, ThreadFactory threadFactory, int moveThreadCount,
            int selectedMoveBufferSize) {
        super(logIndentation, termination, forager);
        this.threadFactory = threadFactory;
        this.moveThreadCount = moveThreadCount;
        this.selectedMoveBufferSize = selectedMoveBufferSize;
    }

    public void setAssertStepScoreFromScratch(boolean assertStepScoreFromScratch) {
        this.assertStepScoreFromScratch = assertStepScoreFromScratch;
    }

    public void setAssertExpectedStepScore(boolean assertExpectedStepScore) {
        this.assertExpectedStepScore = assertExpectedStepScore;
    }

    public void setAssertShadowVariablesAreNotStaleAfterStep(boolean assertShadowVariablesAreNotStaleAfterStep) {
        this.assertShadowVariablesAreNotStaleAfterStep = assertShadowVariablesAreNotStaleAfterStep;
    }

    @Override
    public void phaseStarted(ConstructionHeuristicPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // Capacity: number of moves in circulation + number of setup xor step operations + number of destroy operations
        operationQueue = new ArrayBlockingQueue<>(selectedMoveBufferSize + moveThreadCount + moveThreadCount);
        // Capacity: number of moves in circulation + number of exception handling results
        resultQueue = new OrderByMoveIndexBlockingQueue<>(selectedMoveBufferSize + moveThreadCount);
        moveThreadBarrier = new CyclicBarrier(moveThreadCount);
        InnerScoreDirector<Solution_, ?> scoreDirector = phaseScope.getScoreDirector();
        executor = createThreadPoolExecutor();
        moveThreadRunnerList = new ArrayList<>(moveThreadCount);
        for (int moveThreadIndex = 0; moveThreadIndex < moveThreadCount; moveThreadIndex++) {
            MoveThreadRunner<Solution_, ?> moveThreadRunner = new MoveThreadRunner<>(
                    logIndentation, moveThreadIndex, false,
                    operationQueue, resultQueue, moveThreadBarrier,
                    assertMoveScoreFromScratch, assertExpectedUndoMoveScore,
                    assertStepScoreFromScratch, assertExpectedStepScore, assertShadowVariablesAreNotStaleAfterStep);
            moveThreadRunnerList.add(moveThreadRunner);
            executor.submit(moveThreadRunner);
            operationQueue.add(new SetupOperation<>(scoreDirector));
        }
    }

    @Override
    public void phaseEnded(ConstructionHeuristicPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        // Tell the move thread runners to stop
        // Don't clear the operationsQueue to avoid moveThreadBarrier deadlock:
        // The MoveEvaluationOperations are already cleared and the new ApplyStepOperation isn't added yet.
        DestroyOperation<Solution_> destroyOperation = new DestroyOperation<>();
        for (int i = 0; i < moveThreadCount; i++) {
            operationQueue.add(destroyOperation);
        }
        shutdownMoveThreads();
        long childThreadsScoreCalculationCount = 0;
        for (MoveThreadRunner<Solution_, ?> moveThreadRunner : moveThreadRunnerList) {
            childThreadsScoreCalculationCount += moveThreadRunner.getCalculationCount();
        }
        phaseScope.addChildThreadsScoreCalculationCount(childThreadsScoreCalculationCount);
        operationQueue = null;
        resultQueue = null;
        moveThreadRunnerList = null;
    }

    @Override
    public void solvingError(SolverScope<Solution_> solverScope, Exception exception) {
        super.solvingError(solverScope, exception);
        shutdownMoveThreads();
    }

    protected ExecutorService createThreadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(moveThreadCount,
                threadFactory);
        if (threadPoolExecutor.getMaximumPoolSize() < moveThreadCount) {
            throw new IllegalStateException(
                    "The threadPoolExecutor's maximumPoolSize (" + threadPoolExecutor.getMaximumPoolSize()
                            + ") is less than the moveThreadCount (" + moveThreadCount + "), this is unsupported.");
        }
        return threadPoolExecutor;
    }

    @Override
    public void decideNextStep(ConstructionHeuristicStepScope<Solution_> stepScope, Placement<Solution_> placement) {
        int stepIndex = stepScope.getStepIndex();
        resultQueue.startNextStep(stepIndex);

        int selectMoveIndex = 0;
        int movesInPlay = 0;
        Iterator<Move<Solution_>> moveIterator = placement.iterator();
        do {
            boolean hasNextMove = moveIterator.hasNext();
            // First fill the buffer so move evaluation can run freely in parallel
            // For reproducibility, the selectedMoveBufferSize always need to be entirely selected,
            // even if some of those moves won't end up being evaluated or foraged
            if (movesInPlay > 0 && (selectMoveIndex >= selectedMoveBufferSize || !hasNextMove)) {
                if (forageResult(stepScope, stepIndex)) {
                    break;
                }
                movesInPlay--;
            }
            if (hasNextMove) {
                Move<Solution_> move = moveIterator.next();
                operationQueue.add(new MoveEvaluationOperation<>(stepIndex, selectMoveIndex, move));
                selectMoveIndex++;
                movesInPlay++;
            }
        } while (movesInPlay > 0);

        // Do not evaluate the remaining selected moves for this step that haven't started evaluation yet
        operationQueue.clear();
        pickMove(stepScope);
        // Start doing the step on every move thread. Don't wait for the stepEnded() event.
        if (stepScope.getStep() != null) {
            InnerScoreDirector<Solution_, ?> scoreDirector = stepScope.getScoreDirector();
            if (scoreDirector.requiresFlushing() && stepIndex % 100 == 99) {
                // Calculate score to process changes; otherwise they become a memory leak.
                // We only do it occasionally, as score calculation is a performance cost we do not need to incur here.
                scoreDirector.calculateScore();
            }
            // Increase stepIndex by 1, because it's a preliminary action
            ApplyStepOperation<Solution_, ?> stepOperation = new ApplyStepOperation<>(stepIndex + 1,
                    stepScope.getStep(), (Score) stepScope.getScore());
            for (int i = 0; i < moveThreadCount; i++) {
                operationQueue.add(stepOperation);
            }
        }
    }

    private boolean forageResult(ConstructionHeuristicStepScope<Solution_> stepScope, int stepIndex) {
        OrderByMoveIndexBlockingQueue.MoveResult<Solution_> result;
        try {
            result = resultQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
        if (stepIndex != result.getStepIndex()) {
            throw new IllegalStateException("Impossible situation: the solverThread's stepIndex (" + stepIndex
                    + ") differs from the result's stepIndex (" + result.getStepIndex() + ").");
        }
        Move<Solution_> foragingMove = result.getMove().rebase(stepScope.getScoreDirector());
        int foragingMoveIndex = result.getMoveIndex();
        ConstructionHeuristicMoveScope<Solution_> moveScope = new ConstructionHeuristicMoveScope<>(stepScope, foragingMoveIndex,
                foragingMove);
        if (!result.isMoveDoable()) {
            throw new IllegalStateException("Impossible situation: Construction Heuristics move is not doable.");
        }
        moveScope.setScore(result.getScore());
        // Every doable move result represents a single score calculation on a move thread.
        moveScope.getScoreDirector().incrementCalculationCount();
        logger.trace("{}        Move index ({}), score ({}), move ({}).",
                logIndentation,
                foragingMoveIndex, moveScope.getScore(), foragingMove);
        forager.addMove(moveScope);
        if (forager.isQuitEarly()) {
            return true;
        }
        stepScope.getPhaseScope().getSolverScope().checkYielding();
        if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
            return true;
        }
        return false;
    }

    private void shutdownMoveThreads() {
        if (executor != null && !executor.isShutdown()) {
            ThreadUtils.shutdownAwaitOrKill(executor, logIndentation, "Multi-threaded Construction Heuristic");
        }
    }
}
