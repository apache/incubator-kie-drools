package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class PooledEntityPlacer<Solution_> extends AbstractEntityPlacer<Solution_> implements EntityPlacer<Solution_> {

    protected final MoveSelector<Solution_> moveSelector;

    public PooledEntityPlacer(MoveSelector<Solution_> moveSelector) {
        this.moveSelector = moveSelector;
        phaseLifecycleSupport.addEventListener(moveSelector);
    }

    @Override
    public Iterator<Placement<Solution_>> iterator() {
        return new PooledEntityPlacingIterator();
    }

    private class PooledEntityPlacingIterator extends UpcomingSelectionIterator<Placement<Solution_>> {

        private PooledEntityPlacingIterator() {
        }

        @Override
        protected Placement<Solution_> createUpcomingSelection() {
            Iterator<Move<Solution_>> moveIterator = moveSelector.iterator();
            if (!moveIterator.hasNext()) {
                return noUpcomingSelection();
            }
            return new Placement<Solution_>(moveIterator);
        }

    }

}
