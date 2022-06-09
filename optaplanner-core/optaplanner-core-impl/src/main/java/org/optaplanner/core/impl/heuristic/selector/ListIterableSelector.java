package org.optaplanner.core.impl.heuristic.selector;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.ListIterable;

public interface ListIterableSelector<Solution_, T> extends IterableSelector<Solution_, T>, ListIterable<T> {

}
