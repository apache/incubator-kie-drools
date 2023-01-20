package org.optaplanner.core.impl.heuristic.selector.value;

import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;

/**
 * Abstract superclass for {@link ValueSelector}.
 * It is expected that if two instances share the same properties,
 * they are {@link Object#equals(Object) equal} to one another.
 * This is necessary for proper performance of caches, such as pillar cache or nearby distance matrix cache.
 *
 * @see ValueSelector
 */
public abstract class AbstractValueSelector<Solution_> extends AbstractSelector<Solution_>
        implements ValueSelector<Solution_> {

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

}
