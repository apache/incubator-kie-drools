package org.optaplanner.core.impl.heuristic.selector.move;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;

/**
 * Generates {@link Move}s.
 *
 * @see AbstractMoveSelector
 */
public interface MoveSelector<Solution_> extends IterableSelector<Solution_, Move<Solution_>> {

    default boolean supportsPhaseAndSolverCaching() {
        return false;
    }

}
