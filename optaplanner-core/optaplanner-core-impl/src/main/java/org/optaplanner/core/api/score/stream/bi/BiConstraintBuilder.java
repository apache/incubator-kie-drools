package org.optaplanner.core.api.score.stream.bi;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintBuilder;

/**
 * Used to build a {@link Constraint} out of a {@link BiConstraintStream}, applying optional configuration.
 * To build the constraint, use one of the terminal operations, such as {@link #asConstraint(String)}.
 */
public interface BiConstraintBuilder<A, B> extends ConstraintBuilder<BiConstraintBuilder<A, B>> {

}
