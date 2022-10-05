package org.optaplanner.constraint.streams.common.bi;

import java.util.Collection;
import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.common.ConstraintConstructor;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;

@FunctionalInterface
public interface BiConstraintConstructor<A, B, Score_ extends Score<Score_>>
        extends ConstraintConstructor<Score_, TriFunction<A, B, Score_, Object>, BiFunction<A, B, Collection<?>>> {

}
