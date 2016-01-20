/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * A CompositeMove is composed out of multiple other moves.
 * <p>
 * Warning: each of moves in the moveList must not rely on the effect of a previous move in the moveList
 * to create its undoMove correctly.
 * @see Move
 */
public class CompositeMove implements Move {

    /**
     * @param moves never null, sometimes empty. Do not modify this argument afterwards or the CompositeMove corrupts.
     * @return never null
     */
    public static Move buildMove(Move... moves) {
        int size = moves.length;
        if (size > 1) {
            return new CompositeMove(moves);
        } else if (size == 1) {
            return moves[0];
        } else {
            return new NoChangeMove();
        }
    }

    /**
     * @param moveList never null, sometimes empty
     * @return never null
     */
    public static Move buildMove(List<Move> moveList) {
        int size = moveList.size();
        if (size > 1) {
            return new CompositeMove(moveList.toArray(new Move[0]));
        } else if (size == 1) {
            return moveList.get(0);
        } else {
            return new NoChangeMove();
        }
    }

    // ************************************************************************
    // Non-static members
    // ************************************************************************

    protected final Move[] moves;

    /**
     * @param moves never null, never empty. Do not modify this argument afterwards or this CompositeMove corrupts.
     */
    public CompositeMove(Move... moves) {
        this.moves = moves;
    }

    public Move[] getMoves() {
        return moves;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        for (Move move : moves) {
            if (!move.isMoveDoable(scoreDirector)) {
                return false;
            }
        }
        return true;
    }

    public CompositeMove createUndoMove(ScoreDirector scoreDirector) {
        Move[] undoMoves = new Move[moves.length];
        for (int i = 0; i < moves.length; i++) {
            // Note: this undoMove creation doesn't have the effect yet of a previous move in the moveList
            Move undoMove = moves[i].createUndoMove(scoreDirector);
            undoMoves[moves.length - 1 - i] = undoMove;
        }
        return new CompositeMove(undoMoves);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (Move move : moves) {
            // Calls scoreDirector.triggerVariableListeners() between moves
            // because a later move can depend on the shadow variables changed by an earlier move
            move.doMove(scoreDirector);
        }
        // No need to call scoreDirector.triggerVariableListeners() because Move.doMove() already does it for every move.
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    public String getSimpleMoveTypeDescription() {
        Set<String> childMoveTypeDescriptionSet = new TreeSet<String>();
        for (Move move : moves) {
            childMoveTypeDescriptionSet.add(move.getSimpleMoveTypeDescription());
        }
        StringBuilder moveTypeDescription = new StringBuilder(20 * (moves.length + 1));
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
        Set<Object> entities = new LinkedHashSet<Object>(moves.length * 2);
        for (Move move : moves) {
            entities.addAll(move.getPlanningEntities());
        }
        return entities;
    }

    public Collection<? extends Object> getPlanningValues() {
        Set<Object> values = new LinkedHashSet<Object>(moves.length * 2);
        for (Move move : moves) {
            values.addAll(move.getPlanningValues());
        }
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CompositeMove) {
            CompositeMove other = (CompositeMove) o;
            return Arrays.equals(moves, other.moves);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(moves);
    }

    public String toString() {
        return Arrays.toString(moves);
    }

}
