package org.optaplanner.core.api.domain.constraintweight;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * Allows end users to change the constraint weights, by not hard coding them.
 * This annotation specifies that the class holds a number of {@link ConstraintWeight} annotated members.
 * That class must also have a {@link ConstraintWeight weight} for each of the constraints.
 * <p>
 * A {@link PlanningSolution} has at most one field or property annotated with {@link ConstraintConfigurationProvider}
 * with returns a type of the {@link ConstraintConfiguration} annotated class.
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ConstraintConfiguration {

    /**
     * The namespace of the constraints.
     * <p>
     * This is the default for every {@link ConstraintWeight#constraintPackage()} in the annotated class.
     *
     * @return defaults to the annotated class's package.
     */
    String constraintPackage() default "";

}
