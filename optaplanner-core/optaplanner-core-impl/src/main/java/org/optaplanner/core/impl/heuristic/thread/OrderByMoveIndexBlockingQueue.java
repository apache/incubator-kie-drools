package org.optaplanner.core.impl.heuristic.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;

public class OrderByMoveIndexBlockingQueue<Solution_> {

    private final BlockingQueue<MoveResult<Solution_>> innerQueue;
    private final Map<Integer, MoveResult<Solution_>> backlog;

    private int filterStepIndex = Integer.MIN_VALUE;
    private int nextMoveIndex = Integer.MIN_VALUE;

    public OrderByMoveIndexBlockingQueue(int capacity) {
        innerQueue = new ArrayBlockingQueue<>(capacity);
        backlog = new HashMap<>(capacity);
    }

    /**
     * Not thread-safe. Can only be called from the solver thread.
     *
     * @param stepIndex at least 0
     */
    public void startNextStep(int stepIndex) {
        synchronized (this) {
            if (filterStepIndex >= stepIndex) {
                throw new IllegalStateException("The old filterStepIndex (" + filterStepIndex
                        + ") must be less than the stepIndex (" + stepIndex + ")");
            }
            filterStepIndex = stepIndex;
            MoveResult<Solution_> exceptionResult = innerQueue.stream().filter(MoveResult::hasThrownException)
                    .findFirst().orElse(null);
            if (exceptionResult != null) {
                throw new IllegalStateException("The move thread with moveThreadIndex ("
                        + exceptionResult.getMoveThreadIndex() + ") has thrown an exception."
                        + " Relayed here in the parent thread.",
                        exceptionResult.getThrowable());
            }
            innerQueue.clear();
        }
        nextMoveIndex = 0;
        backlog.clear();
    }

    /**
     * This method is thread-safe. It can be called from any move thread.
     *
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
     *
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
     *
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
     *
     * @return never null
     * @throws InterruptedException if interrupted
     * @see BlockingQueue#take()
     */
    public MoveResult<Solution_> take() throws InterruptedException {
        int moveIndex = nextMoveIndex;
        nextMoveIndex++;
        if (!backlog.isEmpty()) {
            MoveResult<Solution_> result = backlog.remove(moveIndex);
            if (result != null) {
                return result;
            }
        }
        while (true) {
            MoveResult<Solution_> result = innerQueue.take();
            // If 2 exceptions are added from different threads concurrently, either one could end up first.
            // This is a known deviation from 100% reproducibility, that never occurs in a success scenario.
            if (result.hasThrownException()) {
                throw new IllegalStateException("The move thread with moveThreadIndex ("
                        + result.getMoveThreadIndex() + ") has thrown an exception."
                        + " Relayed here in the parent thread.",
                        result.getThrowable());
            }
            if (result.getMoveIndex() == moveIndex) {
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

        public MoveResult(int moveThreadIndex, int stepIndex, int moveIndex, Move<Solution_> move, boolean moveDoable,
                Score score) {
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
