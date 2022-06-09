package org.optaplanner.core.api.score.stream.penta;

import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;

/**
 * Created with {@link Joiners}.
 * Used by {@link QuadConstraintStream#ifExists(Class, PentaJoiner)}, ...
 *
 * @see Joiners
 */
public interface PentaJoiner<A, B, C, D, E> {

    PentaJoiner<A, B, C, D, E> and(PentaJoiner<A, B, C, D, E> otherJoiner);

}
