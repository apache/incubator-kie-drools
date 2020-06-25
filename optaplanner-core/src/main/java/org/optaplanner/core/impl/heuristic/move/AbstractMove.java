/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
        doMoveOnGenuineVariables(scoreDirector);
        scoreDirector.triggerVariableListeners();
        return undoMove;
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
     * Like {@link #doMove(ScoreDirector)} but without the {@link ScoreDirector#triggerVariableListeners()} call
     * (because {@link #doMove(ScoreDirector)} already does that).
     *
     * @param scoreDirector never null
     */
    protected abstract void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector);

    // ************************************************************************
    // Util methods
    // ************************************************************************

    protected static <E> List<E> rebaseList(List<E> externalObjectList, ScoreDirector<?> destinationScoreDirector) {
        List<E> rebasedObjectList = new ArrayList<>(externalObjectList.size());
        for (E entity : externalObjectList) {
            rebasedObjectList.add(destinationScoreDirector.lookUpWorkingObject(entity));
        }
        return rebasedObjectList;
    }

    protected static Object[] rebaseArray(Object[] externalObjects, ScoreDirector<?> destinationScoreDirector) {
        Object[] rebasedObjects = new Object[externalObjects.length];
        for (int i = 0; i < externalObjects.length; i++) {
            rebasedObjects[i] = destinationScoreDirector.lookUpWorkingObject(externalObjects[i]);
        }
        return rebasedObjects;
    }

}
