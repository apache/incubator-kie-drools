package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

/**
 * Specifies that a bean property (or a field) is a custom shadow variable that is updated by another shadow variable's
 * variable listener.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PiggybackShadowVariable {

    /**
     * The {@link PlanningEntity} class of the shadow variable with a variable listener.
     * <p>
     * Specified if the referenced shadow variable is on a different {@link Class} than the class that uses this annotation.
     *
     * @return {@link NullEntityClass} when it is null (workaround for annotation limitation).
     *         Defaults to the same {@link Class} as the one that uses this annotation.
     */
    Class<?> shadowEntityClass() default NullEntityClass.class;

    /**
     * The shadow variable name.
     *
     * @return never null, a genuine or shadow variable name
     */
    String shadowVariableName();

    /** Workaround for annotation limitation in {@link #shadowEntityClass()}. */
    interface NullEntityClass {
    }
}
