package org.optaplanner.core.api.score.stream.tri;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintBuilder;

/**
 * Used to build a {@link Constraint} out of a {@link TriConstraintStream}, applying optional configuration.
 * To build the constraint, use one of the terminal operations, such as {@link #asConstraint(String)}.
 */
public interface TriConstraintBuilder<A, B, C> extends ConstraintBuilder<TriConstraintBuilder<A, B, C>> {

}
