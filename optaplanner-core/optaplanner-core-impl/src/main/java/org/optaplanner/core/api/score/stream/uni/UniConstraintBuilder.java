package org.optaplanner.core.api.score.stream.uni;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintBuilder;

/**
 * Used to build a {@link Constraint} out of a {@link UniConstraintStream}, applying optional configuration.
 * To build the constraint, use one of the terminal operations, such as {@link #asConstraint(String)}.
 */
public interface UniConstraintBuilder<A> extends ConstraintBuilder<UniConstraintBuilder<A>> {

}
