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

package org.optaplanner.core.impl.heuristic.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveThreadRunner<Solution_> implements Runnable {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final String logIndentation;
    private final int moveThreadIndex;
    private final boolean evaluateDoable;

    private final BlockingQueue<MoveThreadOperation<Solution_>> operationQueue;
    private final OrderByMoveIndexBlockingQueue<Solution_> resultQueue;
    private final CyclicBarrier moveThreadBarrier;

    private final boolean assertMoveScoreFromScratch;
    private final boolean assertExpectedUndoMoveScore;
    private final boolean assertStepScoreFromScratch;
    private final boolean assertExpectedStepScore;
    private final boolean assertShadowVariablesAreNotStaleAfterStep;

    private InnerScoreDirector<Solution_> scoreDirector = null;
    private AtomicLong calculationCount = new AtomicLong(-1);

    public MoveThreadRunner(String logIndentation, int moveThreadIndex, boolean evaluateDoable,
            BlockingQueue<MoveThreadOperation<Solution_>> operationQueue,
            OrderByMoveIndexBlockingQueue<Solution_> resultQueue,
            CyclicBarrier moveThreadBarrier,
            boolean assertMoveScoreFromScratch, boolean assertExpectedUndoMoveScore,
            boolean assertStepScoreFromScratch, boolean assertExpectedStepScore, boolean assertShadowVariablesAreNotStaleAfterStep) {
        this.logIndentation = logIndentation;
        this.moveThreadIndex = moveThreadIndex;
        this.evaluateDoable = evaluateDoable;
        this.operationQueue = operationQueue;
        this.resultQueue = resultQueue;
        this.moveThreadBarrier = moveThreadBarrier;
        this.assertMoveScoreFromScratch = assertMoveScoreFromScratch;
        this.assertExpectedUndoMoveScore = assertExpectedUndoMoveScore;
        this.assertStepScoreFromScratch = assertStepScoreFromScratch;
        this.assertExpectedStepScore = assertExpectedStepScore;
        this.assertShadowVariablesAreNotStaleAfterStep = assertShadowVariablesAreNotStaleAfterStep;
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
                        // Don't consume another operation until every moveThread took this SetupOperation
                        moveThreadBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else if (operation instanceof DestroyOperation) {
                    logger.trace("{}            Move thread ({}) destroy: step index ({}).",
                            logIndentation, moveThreadIndex, stepIndex);
                    calculationCount.set(scoreDirector.getCalculationCount());
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
                        // Don't consume an MoveEvaluationOperation until every moveThread took this ApplyStepOperation
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
                    if (evaluateDoable && !move.isMoveDoable(scoreDirector)) {
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
            logger.trace("{}            Move thread ({}) finished.", logIndentation, moveThreadIndex);
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

    protected void predictWorkingStepScore(Move<Solution_> step, Score score) {
        // There is no need to recalculate the score, but we still need to set it
        scoreDirector.getSolutionDescriptor().setScore(scoreDirector.getWorkingSolution(), score);
        if (assertStepScoreFromScratch) {
            scoreDirector.assertPredictedScoreFromScratch(score, step);
        }
        if (assertExpectedStepScore) {
            scoreDirector.assertExpectedWorkingScore(score, step);
        }
        if (assertShadowVariablesAreNotStaleAfterStep) {
            scoreDirector.assertShadowVariablesAreNotStale(score, step);
        }
    }

    /**
     * This method is thread-safe.
     * @return at least 0
     */
    public long getCalculationCount() {
        long calculationCount = this.calculationCount.get();
        if (calculationCount == -1L) {
            logger.info("{}Score calculation speed will be too low"
                    + " because move thread ({})'s destroy wasn't processed soon enough.", logIndentation);
            return 0L;
        }
        return calculationCount;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + moveThreadIndex;
    }

}
