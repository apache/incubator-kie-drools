package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.solver.Solver;

/**
 * Specifies that a bean property (or a field) is an index of this planning value in another entity's
 * {@link PlanningListVariable}.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 * <p>
 * The source variable must be a {@link PlanningListVariable list variable}.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface IndexShadowVariable {

    /**
     * The source variable must be a {@link PlanningListVariable list variable}.
     * <p>
     * When the {@link Solver} changes a genuine variable, it adjusts the shadow variable accordingly.
     * In practice, the {@link Solver} ignores shadow variables (except for consistency housekeeping).
     *
     * @return property name of the list variable that contains instances of this planning value
     */
    String sourceVariableName();
}
