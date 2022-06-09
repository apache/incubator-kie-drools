package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.solver.Solver;

/**
 * Specifies that a bean property (or a field) is the inverse of a {@link PlanningVariable}, which implies it's a shadow
 * variable.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface InverseRelationShadowVariable {

    /**
     * In a bidirectional relationship, the shadow side (= the follower side) uses this property
     * (and nothing else) to declare for which {@link PlanningVariable} (= the leader side) it is a shadow.
     * <p>
     * Both sides of a bidirectional relationship should be consistent: if A points to B, then B must point to A.
     * <p>
     * When the {@link Solver} changes a genuine variable, it adjusts the shadow variable accordingly.
     * In practice, the {@link Solver} ignores shadow variables (except for consistency housekeeping).
     *
     * @return the variable property name on the opposite end of this bidirectional relationship
     */
    String sourceVariableName();

}
