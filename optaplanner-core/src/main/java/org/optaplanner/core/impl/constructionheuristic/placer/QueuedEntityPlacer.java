package org.optaplanner.core.impl.constructionheuristic.placer;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class QueuedEntityPlacer extends AbstractEntityPlacer implements EntityPlacer {

    protected final EntitySelector entitySelector;
    protected final List<MoveSelector> moveSelectorList;

    public QueuedEntityPlacer(EntitySelector entitySelector, List<MoveSelector> moveSelectorList) {
        this.entitySelector = entitySelector;
        this.moveSelectorList = moveSelectorList;
        phaseLifecycleSupport.addEventListener(entitySelector);
        for (MoveSelector moveSelector : moveSelectorList) {
            phaseLifecycleSupport.addEventListener(moveSelector);
        }
    }

    public Iterator<Placement> iterator() {
        return new QueuedEntityPlacingIterator(entitySelector.iterator());
    }

    private class QueuedEntityPlacingIterator extends UpcomingSelectionIterator<Placement> {

        private final Iterator<Object> entityIterator;
        private Iterator<MoveSelector> moveSelectorIterator;

        private QueuedEntityPlacingIterator(Iterator<Object> entityIterator) {
            this.entityIterator = entityIterator;
            moveSelectorIterator = Iterators.emptyIterator();
        }

        protected Placement createUpcomingSelection() {
            Iterator<Move> moveIterator = null;
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
                MoveSelector moveSelector = moveSelectorIterator.next();
                moveIterator = moveSelector.iterator();
            }
            return new Placement(moveIterator);
        }

    }

}
