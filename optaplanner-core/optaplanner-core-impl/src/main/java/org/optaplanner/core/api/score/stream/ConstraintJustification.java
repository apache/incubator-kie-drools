package org.optaplanner.core.api.score.stream;

import org.optaplanner.core.api.score.constraint.ConstraintMatch;

/**
 * Marker interface for constraint justifications.
 * All classes used as constraint justifications must implement this interface.
 *
 * <p>
 * No two instances of such implementing class should be equal,
 * as it is possible for the same constraint to be justified twice by the same facts.
 * (Such as in the case of a non-distinct {@link ConstraintStream}.)
 *
 * <p>
 * Implementing classes may decide to implement {@link Comparable}
 * to preserve order of instances when displayed in user interfaces, logs etc.
 *
 * @see ConstraintMatch#getJustification()
 */
public interface ConstraintJustification {

}
