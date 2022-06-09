package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.solver.Solver;

/**
 * Specifies that a bean property (or a field) is the anchor of a chained {@link PlanningVariable}, which implies it's a shadow
 * variable.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface AnchorShadowVariable {

    /**
     * The source planning variable is a chained planning variable that leads to the anchor.
     * <p>
     * Both the genuine variable and the shadow variable should be consistent:
     * if A chains to B, then A must have the same anchor as B (unless B is the anchor).
     * <p>
     * When the {@link Solver} changes a genuine variable, it adjusts the shadow variable accordingly.
     * In practice, the {@link Solver} ignores shadow variables (except for consistency housekeeping).
     *
     * @return the variable property name on this entity class that leads to the anchor
     */
    String sourceVariableName();

}
