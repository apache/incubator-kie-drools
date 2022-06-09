package org.optaplanner.core.impl.heuristic.selector.entity;

import java.util.Iterator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.ListIterableSelector;

/**
 * Selects instances of 1 {@link PlanningEntity} annotated class.
 *
 * @see AbstractEntitySelector
 * @see FromSolutionEntitySelector
 */
public interface EntitySelector<Solution_> extends ListIterableSelector<Solution_, Object> {

    /**
     * @return never null
     */
    EntityDescriptor<Solution_> getEntityDescriptor();

    /**
     * If {@link #isNeverEnding()} is true, then {@link #iterator()} will never end.
     * This returns an ending {@link Iterator}, that tries to match {@link #iterator()} as much as possible,
     * but returns each distinct element only once and returns every element that might possibly be selected
     * and therefore it might not respect the configuration of this {@link EntitySelector} entirely.
     *
     * @return never null
     * @see #iterator()
     */
    Iterator<Object> endingIterator();

}
