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
     * Thread safe. Can be called from any move thread.
     * @param element never null
     * @see BlockingQueue#add(Object)
     */
    public void add(MoveResult<Solution_> element) {
        synchronized (this) {
            if (element.getStepIndex() != filterStepIndex) {
                // Discard element from previous step
                return;
            }
            innerQueue.add(element);
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
            MoveResult<Solution_> element = backlog.remove(searchMoveIndex);
            if (element != null) {
                return element;
            }
        }
        while (true) {
            MoveResult<Solution_> element = innerQueue.take();
            if (element.getMoveIndex() == searchMoveIndex) {
                return element;
            } else {
                backlog.put(element.getMoveIndex(), element);
            }
        }
    }

    public static class MoveResult<Solution_> {

        private final int stepIndex;
        private final int moveIndex;
        private final Move<Solution_> move;
        private final boolean moveDoable;
        private final Score score;

        public MoveResult(int stepIndex, int moveIndex, Move<Solution_> move, boolean moveDoable, Score score) {

            this.stepIndex = stepIndex;
            this.moveIndex = moveIndex;
            this.move = move;
            this.moveDoable = moveDoable;
            this.score = score;
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
    }

}
