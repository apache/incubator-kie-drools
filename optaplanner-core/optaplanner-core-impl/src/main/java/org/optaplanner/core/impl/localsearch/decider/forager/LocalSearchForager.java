package org.optaplanner.core.impl.localsearch.decider.forager;

import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.localsearch.decider.LocalSearchDecider;
import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Collects the moves and picks the next step from those for the {@link LocalSearchDecider}.
 *
 * @see AbstractLocalSearchForager
 */
public interface LocalSearchForager<Solution_> extends LocalSearchPhaseLifecycleListener<Solution_> {

    /**
     * @return true if it can be combined with a {@link MoveSelector#isNeverEnding()} that returns true.
     */
    boolean supportsNeverEndingMoveSelector();

    /**
     * @param moveScope never null
     */
    void addMove(LocalSearchMoveScope<Solution_> moveScope);

    /**
     * @return true if no further moves should be selected (and evaluated) for this step.
     */
    boolean isQuitEarly();

    /**
     * @param stepScope never null
     * @return sometimes null, for example if no move is selected
     */
    LocalSearchMoveScope<Solution_> pickMove(LocalSearchStepScope<Solution_> stepScope);

}
