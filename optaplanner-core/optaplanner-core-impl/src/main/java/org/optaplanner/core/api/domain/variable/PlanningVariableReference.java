package org.optaplanner.core.api.domain.variable;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

/**
 * A reference to a genuine {@link PlanningVariable} or a shadow variable.
 */
public @interface PlanningVariableReference {

    /**
     * The {@link PlanningEntity} class of the planning variable.
     * <p>
     * Specified if the planning variable is on a different {@link Class}
     * than the class that uses this referencing annotation.
     *
     * @return {@link NullEntityClass} when it is null (workaround for annotation limitation).
     *         Defaults to the same {@link Class} as the one that uses this annotation.
     */
    Class<?> entityClass() default NullEntityClass.class;

    /** Workaround for annotation limitation in {@link #entityClass()}. */
    interface NullEntityClass {
    }

    /**
     * The name of the planning variable that is referenced.
     *
     * @return never null, a genuine or shadow variable name
     */
    String variableName();

}
