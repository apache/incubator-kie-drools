package org.optaplanner.core.impl.heuristic.move;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Abstract superclass for {@link Move}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Move
 */
public abstract class AbstractMove<Solution_> implements Move<Solution_> {

    @Override
    public final AbstractMove<Solution_> doMove(ScoreDirector<Solution_> scoreDirector) {
        AbstractMove<Solution_> undoMove = createUndoMove(scoreDirector);
        doMoveOnly(scoreDirector);
        return undoMove;
    }

    @Override
    public final void doMoveOnly(ScoreDirector<Solution_> scoreDirector) {
        doMoveOnGenuineVariables(scoreDirector);
        scoreDirector.triggerVariableListeners();
    }

    /**
     * Called before the move is done, so the move can be evaluated and then be undone
     * without resulting into a permanent change in the solution.
     *
     * @param scoreDirector the {@link ScoreDirector} not yet modified by the move.
     * @return an undoMove which does the exact opposite of this move.
     */
    protected abstract AbstractMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector);

    /**
     * Like {@link #doMoveOnly(ScoreDirector)} but without the {@link ScoreDirector#triggerVariableListeners()} call
     * (because {@link #doMoveOnly(ScoreDirector)} already does that).
     *
     * @param scoreDirector never null
     */
    protected abstract void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector);

    // ************************************************************************
    // Util methods
    // ************************************************************************

    public static <E> List<E> rebaseList(List<E> externalObjectList, ScoreDirector<?> destinationScoreDirector) {
        List<E> rebasedObjectList = new ArrayList<>(externalObjectList.size());
        for (E entity : externalObjectList) {
            rebasedObjectList.add(destinationScoreDirector.lookUpWorkingObject(entity));
        }
        return rebasedObjectList;
    }

    public static Object[] rebaseArray(Object[] externalObjects, ScoreDirector<?> destinationScoreDirector) {
        Object[] rebasedObjects = new Object[externalObjects.length];
        for (int i = 0; i < externalObjects.length; i++) {
            rebasedObjects[i] = destinationScoreDirector.lookUpWorkingObject(externalObjects[i]);
        }
        return rebasedObjects;
    }

}
