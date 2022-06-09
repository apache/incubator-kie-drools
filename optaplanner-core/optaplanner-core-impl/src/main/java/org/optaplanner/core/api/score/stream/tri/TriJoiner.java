package org.optaplanner.core.api.score.stream.tri;

import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;

/**
 * Created with {@link Joiners}.
 * Used by {@link BiConstraintStream#join(Class, TriJoiner)}, ...
 *
 * @see Joiners
 */
public interface TriJoiner<A, B, C> {

    TriJoiner<A, B, C> and(TriJoiner<A, B, C> otherJoiner);

}
