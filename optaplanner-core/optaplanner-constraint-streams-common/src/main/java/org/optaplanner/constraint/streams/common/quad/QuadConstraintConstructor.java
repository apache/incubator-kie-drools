package org.optaplanner.constraint.streams.common.quad;

import java.util.Collection;

import org.optaplanner.constraint.streams.common.ConstraintConstructor;
import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.Score;

@FunctionalInterface
public interface QuadConstraintConstructor<A, B, C, D, Score_ extends Score<Score_>>
        extends
        ConstraintConstructor<Score_, PentaFunction<A, B, C, D, Score_, Object>, QuadFunction<A, B, C, D, Collection<?>>> {

}
