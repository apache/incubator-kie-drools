package org.optaplanner.core.api.score.stream.bi;

import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

/**
 * Created with {@link Joiners}.
 * Used by {@link UniConstraintStream#join(Class, BiJoiner)}, ...
 *
 * @see Joiners
 */
public interface BiJoiner<A, B> {

    BiJoiner<A, B> and(BiJoiner<A, B> otherJoiner);

}
