package org.optaplanner.core.impl.heuristic.selector;

import org.optaplanner.core.impl.domain.variable.supply.Demand;

/**
 * It is expected that if two instances share the same properties,
 * they are {@link Object#equals(Object) equal} to one another.
 * This is necessary for proper performance of {@link Demand}-based caches,
 * such as pillar cache or nearby distance matrix cache.
 *
 * @param <Solution_>
 */
public abstract class AbstractDemandEnabledSelector<Solution_> extends AbstractSelector<Solution_> {

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

}
