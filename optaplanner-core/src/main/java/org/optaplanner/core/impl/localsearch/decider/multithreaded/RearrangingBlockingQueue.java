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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;

public class RearrangingBlockingQueue<Solution_> {

    private final BlockingQueue<MoveResult<Solution_>> innerQueue;
    private final Map<Integer, MoveResult<Solution_>> backlog;

    private int filterStepIndex = -1;
    private int searchMoveIndex = -1;

    public RearrangingBlockingQueue(int capacity) {
        innerQueue = new ArrayBlockingQueue<>(capacity);
        backlog = new HashMap<>(capacity);
    }

    /**
     * Not thread-safe. Can only be called from the solver thread.
     * @param stepIndex at least 0
     */
    public void startNextStep(int stepIndex) {
        synchronized (this) {
            if (filterStepIndex == stepIndex) {
                throw new IllegalStateException("The filterStepIndex (" + filterStepIndex
                        + ") cannot be the same as the stepIndex (" + stepIndex + ")");
            }
            filterStepIndex = stepIndex;
            innerQueue.clear();
        }
        searchMoveIndex = 0;
        backlog.clear();
    }

    /**
     * This method is thread-safe. It can be called from any move thread.
     * @param moveThreadIndex {@code 0 <= moveThreadIndex < moveThreadCount}
     * @param stepIndex at least 0
     * @param moveIndex at least 0
     * @param move never null
     * @see BlockingQueue#add(Object)
     */
    public void addUndoableMove(int moveThreadIndex, int stepIndex, int moveIndex, Move<Solution_> move) {
        MoveResult<Solution_> result = new MoveResult<>(moveThreadIndex, stepIndex, moveIndex, move, false, null);
        synchronized (this) {
            if (result.getStepIndex() != filterStepIndex) {
                // Discard element from previous step
                return;
            }
            innerQueue.add(result);
        }
    }

    /**
     * This method is thread-safe. It can be called from any move thread.
     * @param moveThreadIndex {@code 0 <= moveThreadIndex < moveThreadCount}
     * @param stepIndex at least 0
     * @param moveIndex at least 0
     * @param move never null
     * @param score never null
     * @see BlockingQueue#add(Object)
     */
    public void addMove(int moveThreadIndex, int stepIndex, int moveIndex, Move<Solution_> move, Score score) {
        MoveResult<Solution_> result = new MoveResult<>(moveThreadIndex, stepIndex, moveIndex, move, true, score);
        synchronized (this) {
            if (result.getStepIndex() != filterStepIndex) {
                // Discard element from previous step
                return;
            }
            innerQueue.add(result);
        }
    }

    /**
     * This method is thread-safe. It can be called from any move thread.
     * Previous results (that haven't been consumed yet), will still be returned during iteration
     * before the iteration throws an exception,
     * unless there's a lower moveIndex that isn't in the queue yet.
     * @param moveThreadIndex {@code 0 <= moveThreadIndex < moveThreadCount}
     * @param throwable never null
     */
    public void addExceptionThrown(int moveThreadIndex, Throwable throwable) {
        MoveResult<Solution_> result = new MoveResult<>(moveThreadIndex, throwable);
        synchronized (this) {
            innerQueue.add(result);
        }
    }

    /**
     * Not thread-safe. Can only be called from the solver thread.
     * @return never null
     * @throws InterruptedException if interrupted
     * @see BlockingQueue#take()
     */
    public MoveResult<Solution_> take() throws InterruptedException {
        searchMoveIndex++;
        if (!backlog.isEmpty()) {
            MoveResult<Solution_> result = backlog.remove(searchMoveIndex);
            if (result != null) {
                return result;
            }
        }
        while (true) {
            MoveResult<Solution_> result = innerQueue.take();
            if (result.hasThrownException()) {
                throw new IllegalStateException("The move thread with moveThreadIndex ("
                        + result.getMoveThreadIndex() + ") has thrown an exception."
                        + " Relayed here in the parent thread.",
                        result.getThrowable());
            }
            if (result.getMoveIndex() == searchMoveIndex) {
                return result;
            } else {
                backlog.put(result.getMoveIndex(), result);
            }
        }
    }

    public static class MoveResult<Solution_> {

        private final int moveThreadIndex;
        private final int stepIndex;
        private final int moveIndex;
        private final Move<Solution_> move;
        private final boolean moveDoable;
        private final Score score;
        private final Throwable throwable;

        public MoveResult(int moveThreadIndex, int stepIndex, int moveIndex, Move<Solution_> move, boolean moveDoable, Score score) {
            this.moveThreadIndex = moveThreadIndex;
            this.stepIndex = stepIndex;
            this.moveIndex = moveIndex;
            this.move = move;
            this.moveDoable = moveDoable;
            this.score = score;
            this.throwable = null;
        }

        public MoveResult(int moveThreadIndex, Throwable throwable) {
            this.moveThreadIndex = moveThreadIndex;
            this.stepIndex = -1;
            this.moveIndex = -1;
            this.move = null;
            this.moveDoable = false;
            this.score = null;
            this.throwable = throwable;
        }

        private boolean hasThrownException() {
            return throwable != null;
        }

        public int getMoveThreadIndex() {
            return moveThreadIndex;
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

        public boolean isMoveDoable() {
            return moveDoable;
        }

        public Score getScore() {
            return score;
        }

        private Throwable getThrowable() {
            return throwable;
        }

    }

}
