/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.multithreaded;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.localsearch.decider.LocalSearchDecider;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.forager.LocalSearchForager;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.ChildThreadType;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class MultiThreadedLocalSearchDecider<Solution_> extends LocalSearchDecider<Solution_> {

    protected final ThreadFactory threadFactory;
    protected final int moveThreadCount;
    protected final int selectedMoveBufferSize;

    protected boolean assertStepScoreFromScratch = false;
    protected boolean assertExpectedStepScore = false;
    protected boolean assertShadowVariablesAreNotStaleAfterStep = false;

    protected BlockingQueue<MoveThreadOperation<Solution_>> operationQueue;
    protected RearrangingBlockingQueue<Solution_> resultQueue;
    protected ExecutorService executor;
    protected CyclicBarrier moveThreadBarrier;

    public MultiThreadedLocalSearchDecider(String logIndentation, Termination termination,
            MoveSelector moveSelector, Acceptor acceptor, LocalSearchForager forager,
            ThreadFactory threadFactory, int moveThreadCount, int selectedMoveBufferSize) {
        super(logIndentation, termination, moveSelector, acceptor, forager);
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
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // Capacity: number of moves in circulation + number of setup xor step operations + number of destroy operations
        operationQueue = new ArrayBlockingQueue<>(selectedMoveBufferSize + moveThreadCount + moveThreadCount);
        // Capacity: number of moves in circulation + number of exception handling results
        resultQueue = new RearrangingBlockingQueue<>(selectedMoveBufferSize + moveThreadCount);
        executor = createThreadPoolExecutor();
        moveThreadBarrier = new CyclicBarrier(moveThreadCount);
        InnerScoreDirector<Solution_> scoreDirector = phaseScope.getScoreDirector();
        for (int moveThreadIndex = 0; moveThreadIndex < moveThreadCount; moveThreadIndex++) {
            MoveThreadRunner moveThreadRunner = new MoveThreadRunner(moveThreadIndex); // TODO pass Queue's so phaseEnded can null them
            executor.submit(moveThreadRunner);
            operationQueue.add(new SetupOperation<>(scoreDirector));
        }
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        operationQueue = null;
        resultQueue = null;
        // TODO

    }

    private ExecutorService createThreadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor
                = (ThreadPoolExecutor) Executors.newFixedThreadPool(moveThreadCount, threadFactory);
        if (threadPoolExecutor.getMaximumPoolSize() < moveThreadCount) {
            throw new IllegalStateException(
                    "The threadPoolExecutor's maximumPoolSize (" + threadPoolExecutor.getMaximumPoolSize()
                    + ") is less than the moveThreadCount (" + moveThreadCount + "), this is unsupported.");
        }
        return threadPoolExecutor;
    }

    @Override
    public void decideNextStep(LocalSearchStepScope<Solution_> stepScope) {
        int stepIndex = stepScope.getStepIndex();
        resultQueue.startNextStep(stepIndex);
        int selectingMoveIndex = 0;
        for (Move<Solution_> selectingMove : moveSelector) {
            operationQueue.add(new MoveEvaluationOperation<>(stepIndex, selectingMoveIndex, selectingMove));
            selectingMoveIndex++; // Increment required before if check
            if (selectingMoveIndex < selectedMoveBufferSize) {
                // Fill the buffer so move evaluation can run freely in parallel
                // For reproducibility, the selectedMoveBufferSize always need to be selected,
                // even if some of those moves won't be evaluated or foraged
                continue;
            }
            RearrangingBlockingQueue.MoveResult<Solution_> result;
            try {
                result = resultQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (stepIndex != result.getStepIndex()) {
                throw new IllegalStateException("Impossible situation: the solverThread's stepIndex (" + stepIndex
                        + ") differs from the result's stepIndex (" + result.getStepIndex() + ").");
            }
            Move<Solution_> foragingMove = result.getMove().rebase(stepScope.getScoreDirector());
            int foragingMoveIndex = result.getMoveIndex();
            LocalSearchMoveScope moveScope = new LocalSearchMoveScope<>(stepScope, foragingMoveIndex, foragingMove);
            if (!result.isMoveDoable()) {
                logger.trace("{}        Move index ({}) not doable, ignoring move ({}).",
                        logIndentation, foragingMoveIndex, foragingMove);
            } else {
                moveScope.setScore(result.getScore());
                boolean accepted = acceptor.isAccepted(moveScope);
                moveScope.setAccepted(accepted);
                logger.trace("{}        Move index ({}), score ({}), accepted ({}), move ({}).",
                        logIndentation,
                        foragingMoveIndex, moveScope.getScore(), moveScope.getAccepted(),
                        foragingMove);
                forager.addMove(moveScope);
                if (forager.isQuitEarly()) {
                    break;
                }
            }
            stepScope.getPhaseScope().getSolverScope().checkYielding();
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
        // Do not evaluate the remaining selected moves for this step that haven't started evaluation yet
        operationQueue.clear();
        pickMove(stepScope);
        // Start doing the step on every move thread. Don't wait for the stepEnded() event.
        if (stepScope.getStep() != null) {
            // Increase stepIndex by 1, because it's a preliminary action
            ApplyStepOperation<Solution_> stepOperation = new ApplyStepOperation<>(
                    stepIndex + 1, stepScope.getStep(), stepScope.getScore());
            for (int i = 0; i < moveThreadCount; i++) {
                operationQueue.add(stepOperation);
            }
        }
        // TODO latch barrier
    }

    private static abstract class MoveThreadOperation<Solution_> {

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }

    }

    private static class SetupOperation<Solution_> extends MoveThreadOperation<Solution_> {

        private final InnerScoreDirector<Solution_> innerScoreDirector;

        public SetupOperation(InnerScoreDirector<Solution_> innerScoreDirector) {
            this.innerScoreDirector = innerScoreDirector;
        }

        public InnerScoreDirector<Solution_> getScoreDirector() {
            return innerScoreDirector;
        }
    }

    private static class DestroyOperation<Solution_> extends MoveThreadOperation<Solution_> {

    }

    private static class ApplyStepOperation<Solution_> extends MoveThreadOperation<Solution_> {

        private final int stepIndex;
        private final Move<Solution_> step;
        private final Score score;

        public ApplyStepOperation(int stepIndex, Move<Solution_> step, Score score) {
            this.stepIndex = stepIndex;
            this.step = step;
            this.score = score;
        }

        public int getStepIndex() {
            return stepIndex;
        }

        public Move<Solution_> getStep() {
            return step;
        }

        public Score getScore() {
            return score;
        }
    }

    private static class MoveEvaluationOperation<Solution_> extends MoveThreadOperation<Solution_> {

        private final int stepIndex;
        private final int moveIndex;
        private final Move<Solution_> move;

        public MoveEvaluationOperation(int stepIndex, int moveIndex, Move<Solution_> move) {
            this.stepIndex = stepIndex;
            this.moveIndex = moveIndex;
            this.move = move;
        }

        public int getStepIndex() {
            return stepIndex;
        }

        public int getMoveIndex() {
            return moveIndex;
        }

        public Move<Solution_> getMove() {
            return move;
        }
    }

    private class MoveThreadRunner implements Runnable {

        private int moveThreadIndex;
        private InnerScoreDirector<Solution_> scoreDirector = null;

        public MoveThreadRunner(int moveThreadIndex) {
            this.moveThreadIndex = moveThreadIndex;
        }

        @Override
        public void run() {
            try {
                int stepIndex = -1;
                Score lastStepScore = null;
                while (true) {
                    MoveThreadOperation<Solution_> operation;
                    try {
                        operation = operationQueue.take();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }

                    if (operation instanceof SetupOperation) {
                        SetupOperation<Solution_> setupOperation = (SetupOperation<Solution_>) operation;
                        scoreDirector = setupOperation.getScoreDirector()
                                .createChildThreadScoreDirector(ChildThreadType.MOVE_THREAD);
                        stepIndex = 0;
                        lastStepScore = scoreDirector.calculateScore();
                        logger.trace("{}            Move thread ({}) setup: step index ({}), score ({}).",
                                logIndentation, moveThreadIndex, stepIndex, lastStepScore);
                        try {
                            // Don't consume another operation until every moveThread took his SetupOperation
                            moveThreadBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else if (operation instanceof DestroyOperation) {
                        logger.trace("{}            Move thread ({}) destroy: step index ({}).",
                                logIndentation, moveThreadIndex, stepIndex);
                        break;
                    } else if (operation instanceof ApplyStepOperation) {
                        // TODO Performance gain with specialized 2-phase cyclic barrier:
                        // As soon as the last move thread has taken its ApplyStepOperation,
                        // other move threads can already depart from the moveThreadStepBarrier: no need to wait until the step is done.
                        ApplyStepOperation<Solution_> applyStepOperation = (ApplyStepOperation<Solution_>) operation;
                        if (stepIndex + 1 != applyStepOperation.getStepIndex()) {
                            throw new IllegalStateException("Impossible situation: the moveThread's stepIndex (" + stepIndex
                                    + ") is not followed by the operation's stepIndex ("
                                    + applyStepOperation.getStepIndex() + ").");
                        }
                        stepIndex = applyStepOperation.getStepIndex();
                        Move<Solution_> step = applyStepOperation.getStep().rebase(scoreDirector);
                        Score score = applyStepOperation.getScore();
                        step.doMove(scoreDirector);
                        predictWorkingStepScore(step, score);
                        lastStepScore = score;
                        logger.trace("{}            Move thread ({}) step: step index ({}), score ({}).",
                                logIndentation, moveThreadIndex, stepIndex, lastStepScore);
                        try {
                            // Don't consume an MoveEvaluationOperation until every moveThread took his ApplyStepOperation
                            moveThreadBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else if (operation instanceof MoveEvaluationOperation) {
                        MoveEvaluationOperation<Solution_> moveEvaluationOperation = (MoveEvaluationOperation<Solution_>) operation;
                        int moveIndex = moveEvaluationOperation.getMoveIndex();
                        if (stepIndex != moveEvaluationOperation.getStepIndex()) {
                            throw new IllegalStateException("Impossible situation: the moveThread's stepIndex ("
                                    + stepIndex + ") differs from the operation's stepIndex ("
                                    + moveEvaluationOperation.getStepIndex() + ") with moveIndex ("
                                    + moveIndex + ").");
                        }
                        Move<Solution_> move = moveEvaluationOperation.getMove().rebase(scoreDirector);
                        if (!move.isMoveDoable(scoreDirector)) {
                            logger.trace("{}            Move thread ({}) evaluation: step index ({}), move index ({}), not doable.",
                                    logIndentation, moveThreadIndex, stepIndex, moveIndex);
                            resultQueue.addUndoableMove(moveThreadIndex, stepIndex, moveIndex, move);
                        } else {
                            Score score = scoreDirector.doAndProcessMove(move, assertMoveScoreFromScratch);
                            if (assertExpectedUndoMoveScore) {
                                scoreDirector.assertExpectedUndoMoveScore(move, lastStepScore);
                            }
                            logger.trace("{}            Move thread ({}) evaluation: step index ({}), move index ({}), score ({}).",
                                    logIndentation, moveThreadIndex, stepIndex, moveIndex, score);
                            // Deliberately add to fail fast if there is not enough capacity (which is impossible)
                            resultQueue.addMove(moveThreadIndex, stepIndex, moveIndex, move, score);
                        }
                    } else {
                        throw new IllegalStateException("Unknown operation (" + operation + ").");
                    }
                    // TODO checkYielding();
                }
                logger.trace("{}            Move thread finished.");
            } catch (RuntimeException | Error throwable) {
                // Any Exception or even Error that happens here (on a move thread) must be stored
                // in the resultQueue in order to be propagated to the solver thread.
                logger.trace("{}            Move thread ({}) exception that will be propagated to the solver thread.",
                        logIndentation, moveThreadIndex, throwable);
                resultQueue.addExceptionThrown(moveThreadIndex, throwable);
            } finally {
                if (scoreDirector != null) {
                    scoreDirector.close();
                }
            }
        }

        private void predictWorkingStepScore(Move<Solution_> step, Score score) {
            // There is no need to recalculate the score, but we still need to set it
            scoreDirector.getSolutionDescriptor().setScore(scoreDirector.getWorkingSolution(), score);
            if (assertStepScoreFromScratch) {
                scoreDirector.assertWorkingScoreFromScratch(score, step);
            }
            if (assertExpectedStepScore) {
                scoreDirector.assertExpectedWorkingScore(score, step);
            }
            if (assertShadowVariablesAreNotStaleAfterStep) {
                scoreDirector.assertShadowVariablesAreNotStale(score, step);
            }
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "-" + moveThreadIndex;
        }

    }

}
