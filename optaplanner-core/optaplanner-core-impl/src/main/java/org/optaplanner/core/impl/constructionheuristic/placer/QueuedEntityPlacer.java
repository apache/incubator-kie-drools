package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class QueuedEntityPlacer<Solution_> extends AbstractEntityPlacer<Solution_> implements EntityPlacer<Solution_> {

    protected final EntitySelector<Solution_> entitySelector;
    protected final List<MoveSelector<Solution_>> moveSelectorList;

    public QueuedEntityPlacer(EntitySelector<Solution_> entitySelector, List<MoveSelector<Solution_>> moveSelectorList) {
        this.entitySelector = entitySelector;
        this.moveSelectorList = moveSelectorList;
        phaseLifecycleSupport.addEventListener(entitySelector);
        for (MoveSelector<Solution_> moveSelector : moveSelectorList) {
            phaseLifecycleSupport.addEventListener(moveSelector);
        }
    }

    @Override
    public Iterator<Placement<Solution_>> iterator() {
        return new QueuedEntityPlacingIterator(entitySelector.iterator());
    }

    private class QueuedEntityPlacingIterator extends UpcomingSelectionIterator<Placement<Solution_>> {

        private final Iterator<Object> entityIterator;
        private Iterator<MoveSelector<Solution_>> moveSelectorIterator;

        private QueuedEntityPlacingIterator(Iterator<Object> entityIterator) {
            this.entityIterator = entityIterator;
            moveSelectorIterator = Collections.emptyIterator();
        }

        @Override
        protected Placement<Solution_> createUpcomingSelection() {
            Iterator<Move<Solution_>> moveIterator = null;
            // Skip empty placements to avoid no-operation steps
            while (moveIterator == null || !moveIterator.hasNext()) {
                // If a moveSelector's iterator is empty, it might not be empty the next time
                // (because the entity changes)
                while (!moveSelectorIterator.hasNext()) {
                    if (!entityIterator.hasNext()) {
                        return noUpcomingSelection();
                    }
                    entityIterator.next();
                    moveSelectorIterator = moveSelectorList.iterator();
                }
                MoveSelector<Solution_> moveSelector = moveSelectorIterator.next();
                moveIterator = moveSelector.iterator();
            }
            return new Placement<>(moveIterator);
        }

    }

}
