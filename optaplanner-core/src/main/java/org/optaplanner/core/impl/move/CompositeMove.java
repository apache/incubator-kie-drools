/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * A CompositeMove is composed out of multiple other moves.
 * <p/>
 * Warning: one of the moveList moves should not rely on the effect on of a previous moveList move
 * to create an uncorrupted undoMove.
 * @see Move
 */
public class CompositeMove implements Move {

    protected List<Move> moveList;

    /**
     * @param moveList cannot be null
     */
    public CompositeMove(List<Move> moveList) {
        this.moveList = moveList;
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        for (Move move : moveList) {
            if (!move.isMoveDoable(scoreDirector)) {
                return false;
            }
        }
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        List<Move> undoMoveList = new ArrayList<Move>(moveList.size());
        for (Move move : moveList) {
            // Note: this undoMove doesn't have the affect of a previous move in the moveList
            // This could be made possible by merging the methods createUndoMove and doMove...
            Move undoMove = move.createUndoMove(scoreDirector);
            undoMoveList.add(undoMove);
        }
        Collections.reverse(undoMoveList);
        return new CompositeMove(undoMoveList);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (Move move : moveList) {
            move.doMove(scoreDirector);
        }
    }

    public Collection<? extends Object> getPlanningEntities() {
        Set<Object> entities = new LinkedHashSet<Object>(moveList.size() * 2);
        for (Move move : moveList) {
            entities.addAll(move.getPlanningEntities());
        }
        return entities;
    }

    public Collection<? extends Object> getPlanningValues() {
        Set<Object> values = new LinkedHashSet<Object>(moveList.size() * 2);
        for (Move move : moveList) {
            values.addAll(move.getPlanningValues());
        }
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CompositeMove) {
            CompositeMove other = (CompositeMove) o;
            return moveList.equals(other.moveList);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return moveList.hashCode();
    }

    public String toString() {
        return moveList.toString();
    }

}
