package org.optaplanner.core.impl.heuristic.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * A CompositeMove is composed out of multiple other moves.
 * <p>
 * Warning: each of moves in the moveList must not rely on the effect of a previous move in the moveList
 * to create its undoMove correctly.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Move
 */
public class CompositeMove<Solution_> implements Move<Solution_> {

    /**
     * @param moves never null, sometimes empty. Do not modify this argument afterwards or the CompositeMove corrupts.
     * @return never null
     */
    @SafeVarargs
    public static <Solution_, Move_ extends Move<Solution_>> Move<Solution_> buildMove(Move_... moves) {
        int size = moves.length;
        if (size > 1) {
            return new CompositeMove<>(moves);
        } else if (size == 1) {
            return moves[0];
        } else {
            return new NoChangeMove<>();
        }
    }

    /**
     * @param moveList never null, sometimes empty
     * @return never null
     */
    public static <Solution_, Move_ extends Move<Solution_>> Move<Solution_> buildMove(List<Move_> moveList) {
        int size = moveList.size();
        if (size > 1) {
            return new CompositeMove<>(moveList.toArray(new Move[0]));
        } else if (size == 1) {
            return moveList.get(0);
        } else {
            return new NoChangeMove<>();
        }
    }

    // ************************************************************************
    // Non-static members
    // ************************************************************************

    protected final Move<Solution_>[] moves;

    /**
     * @param moves never null, never empty. Do not modify this argument afterwards or this CompositeMove corrupts.
     */
    @SafeVarargs
    public CompositeMove(Move<Solution_>... moves) {
        this.moves = moves;
    }

    public Move<Solution_>[] getMoves() {
        return moves;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        for (Move<Solution_> move : moves) {
            if (move.isMoveDoable(scoreDirector)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CompositeMove<Solution_> doMove(ScoreDirector<Solution_> scoreDirector) {
        Move<Solution_>[] undoMoves = new Move[moves.length];
        int doableCount = 0;
        for (Move<Solution_> move : moves) {
            if (!move.isMoveDoable(scoreDirector)) {
                continue;
            }
            // Calls scoreDirector.triggerVariableListeners() between moves
            // because a later move can depend on the shadow variables changed by an earlier move
            Move<Solution_> undoMove = move.doMove(scoreDirector);
            // Undo in reverse order and each undoMove is created after previous moves have been done
            undoMoves[moves.length - 1 - doableCount] = undoMove;
            doableCount++;
        }
        if (doableCount < undoMoves.length) {
            undoMoves = Arrays.copyOfRange(undoMoves, undoMoves.length - doableCount, undoMoves.length);
        }
        // No need to call scoreDirector.triggerVariableListeners() because Move.doMove() already does it for every move.
        return new CompositeMove<>(undoMoves);
    }

    @Override
    public CompositeMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        Move<Solution_>[] rebasedMoves = new Move[moves.length];
        for (int i = 0; i < moves.length; i++) {
            rebasedMoves[i] = moves[i].rebase(destinationScoreDirector);
        }
        return new CompositeMove<>(rebasedMoves);
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        Set<String> childMoveTypeDescriptionSet = new TreeSet<>();
        for (Move<Solution_> move : moves) {
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

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        Set<Object> entities = new LinkedHashSet<>(moves.length * 2);
        for (Move<Solution_> move : moves) {
            entities.addAll(move.getPlanningEntities());
        }
        return entities;
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        Set<Object> values = new LinkedHashSet<>(moves.length * 2);
        for (Move<Solution_> move : moves) {
            values.addAll(move.getPlanningValues());
        }
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CompositeMove) {
            CompositeMove<Solution_> other = (CompositeMove<Solution_>) o;
            return Arrays.equals(moves, other.moves);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(moves);
    }

    @Override
    public String toString() {
        return Arrays.toString(moves);
    }

}
