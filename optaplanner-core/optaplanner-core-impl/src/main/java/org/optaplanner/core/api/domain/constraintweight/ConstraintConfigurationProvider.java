package org.optaplanner.core.api.domain.constraintweight;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class is a {@link ConstraintConfiguration}.
 * This property is automatically a {@link ProblemFactProperty} too, so no need to declare that explicitly.
 * <p>
 * The type of this property (or field) must have a {@link ConstraintConfiguration} annotation.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ConstraintConfigurationProvider {

}
