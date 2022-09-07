package org.optaplanner.core.api.score.stream.quad;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintBuilder;

/**
 * Used to build a {@link Constraint} out of a {@link QuadConstraintStream}, applying optional configuration.
 * To build the constraint, use one of the terminal operations, such as {@link #asConstraint(String)}.
 */
public interface QuadConstraintBuilder<A, B, C, D> extends ConstraintBuilder<QuadConstraintBuilder<A, B, C, D>> {

}
