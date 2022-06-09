package org.optaplanner.core.impl.phase.scope;

import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractMoveScope<Solution_> {

    protected final int moveIndex;
    protected final Move<Solution_> move;

    protected Score<?> score = null;

    public AbstractMoveScope(int moveIndex, Move<Solution_> move) {
        this.moveIndex = moveIndex;
        this.move = move;
    }

    public abstract AbstractStepScope<Solution_> getStepScope();

    public int getMoveIndex() {
        return moveIndex;
    }

    public Move<Solution_> getMove() {
        return move;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public int getStepIndex() {
        return getStepScope().getStepIndex();
    }

    public <Score_ extends Score<Score_>> InnerScoreDirector<Solution_, Score_> getScoreDirector() {
        return getStepScope().getScoreDirector();
    }

    public Solution_ getWorkingSolution() {
        return getStepScope().getWorkingSolution();
    }

    public Random getWorkingRandom() {
        return getStepScope().getWorkingRandom();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getStepScope().getStepIndex() + "/" + moveIndex + ")";
    }

}
