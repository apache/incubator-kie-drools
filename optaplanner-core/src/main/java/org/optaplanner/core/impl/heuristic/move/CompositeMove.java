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

package org.optaplanner.core.impl.heuristic.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * A CompositeMove is composed out of multiple other moves.
 * <p/>
 * Warning: each of moves in the moveList must not rely on the effect of a previous move in the moveList
 * to create its undoMove correctly.
 * @see Move
 */
public class CompositeMove extends AbstractMove {

    public static Move buildMove(List<Move> moveList) {
        int size = moveList.size();
        if (size > 1) {
            return new CompositeMove(moveList);
        } else if (size == 1) {
            return moveList.get(0);
        } else {
            return new NoChangeMove();
        }
    }

    protected final List<Move> moveList;

    /**
     * @param moveList never null
     */
    public CompositeMove(List<Move> moveList) {
        this.moveList = moveList;
    }

    /**
     * @return never null
     */
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

    public CompositeMove createUndoMove(ScoreDirector scoreDirector) {
        List<Move> undoMoveList = new ArrayList<Move>(moveList.size());
        for (Move move : moveList) {
            // Note: this undoMove creation doesn't have the effect yet of a previous move in the moveList
            Move undoMove = move.createUndoMove(scoreDirector);
            undoMoveList.add(undoMove);
        }
        return new CompositeMove(Lists.reverse(undoMoveList));
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (Move move : moveList) {
            move.doMove(scoreDirector);
        }
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    public String getSimpleMoveTypeDescription() {
        Set<String> childMoveTypeDescriptionSet = new TreeSet<String>();
        for (Move move : moveList) {
            childMoveTypeDescriptionSet.add(move.getSimpleMoveTypeDescription());
        }
        StringBuilder moveTypeDescription = new StringBuilder(20 * (moveList.size() + 1));
        moveTypeDescription.append(getClass().getSimpleName()).append("(");
        String delimiter = "";
        for (String childMoveTypeDescription : childMoveTypeDescriptionSet) {
            moveTypeDescription.append(delimiter).append("* ").append(childMoveTypeDescription);
            delimiter = ", ";
        }
        moveTypeDescription.append(")");
        return moveTypeDescription.toString();
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
