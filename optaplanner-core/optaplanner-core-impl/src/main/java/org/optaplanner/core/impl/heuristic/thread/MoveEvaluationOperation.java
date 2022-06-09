package org.optaplanner.core.impl.heuristic.thread;

import org.optaplanner.core.impl.heuristic.move.Move;

public class MoveEvaluationOperation<Solution_> extends MoveThreadOperation<Solution_> {

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
