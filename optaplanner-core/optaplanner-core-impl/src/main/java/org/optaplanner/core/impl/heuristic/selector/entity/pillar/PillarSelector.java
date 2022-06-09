package org.optaplanner.core.impl.heuristic.selector.entity.pillar;

import java.util.List;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.ListIterableSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

/**
 * A pillar is a {@link List} of entities that have the same planning value for each (or a subset)
 * of their planning values.
 * Selects a {@link List} of such entities that are moved together.
 *
 * @see EntitySelector
 */
public interface PillarSelector<Solution_> extends ListIterableSelector<Solution_, List<Object>> {

    /**
     * @return never null
     */
    EntityDescriptor<Solution_> getEntityDescriptor();

}
