package org.optaplanner.core.impl.domain.valuerange;

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;

/**
 * Abstract superclass for {@link ValueRange} that is not a {@link CountableValueRange}).
 *
 * @see ValueRange
 * @see ValueRangeFactory
 */
public abstract class AbstractUncountableValueRange<T> implements ValueRange<T> {

}
