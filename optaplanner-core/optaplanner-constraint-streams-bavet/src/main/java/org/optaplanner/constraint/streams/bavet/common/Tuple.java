package org.optaplanner.constraint.streams.bavet.common;

import java.util.function.Function;

import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

/**
 * A tuple must not implement equals()/hashCode() to fact equality,
 * because some stream operations ({@link UniConstraintStream#map(Function)}, ...)
 * might create 2 different tuple instances to contain the same facts.
 */
public interface Tuple {

    BavetTupleState getState();

    void setState(BavetTupleState state);

    Object[] getStore();

}
