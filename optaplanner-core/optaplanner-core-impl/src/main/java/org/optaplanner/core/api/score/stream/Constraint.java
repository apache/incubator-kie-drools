package org.optaplanner.core.api.score.stream;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

/**
 * This represents a single constraint in the {@link ConstraintStream} API
 * that impacts the {@link Score}.
 * It is defined in {@link ConstraintProvider#defineConstraints(ConstraintFactory)}
 * by calling {@link ConstraintFactory#forEach(Class)}.
 */
public interface Constraint {

    /**
     * The {@link ConstraintFactory} that build this.
     *
     * @deprecated for removal as it is not necessary on the public API.
     * @return never null
     */
    @Deprecated(forRemoval = true)
    ConstraintFactory getConstraintFactory();

    /**
     * The constraint package is the namespace of the constraint.
     * <p>
     * When using a {@link ConstraintConfiguration},
     * it is equal to the {@link ConstraintWeight#constraintPackage()}.
     *
     * @return never null
     */
    String getConstraintPackage();

    /**
     * The constraint name.
     * It might not be unique, but {@link #getConstraintId()} is unique.
     * <p>
     * When using a {@link ConstraintConfiguration},
     * it is equal to the {@link ConstraintWeight#value()}.
     *
     * @return never null
     */
    String getConstraintName();

    /**
     * The constraint id is {@link #getConstraintPackage() the constraint package}
     * concatenated with "/" and {@link #getConstraintName() the constraint name}.
     * It is unique.
     *
     * @return never null
     */
    default String getConstraintId() {
        return ConstraintMatchTotal.composeConstraintId(getConstraintPackage(), getConstraintName());
    }

}
