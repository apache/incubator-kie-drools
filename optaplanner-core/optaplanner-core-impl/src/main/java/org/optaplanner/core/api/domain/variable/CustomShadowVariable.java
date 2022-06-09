package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

/**
 * Specifies that a bean property (or a field) is a custom shadow of 1 or more {@link PlanningVariable}'s.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface CustomShadowVariable {

    /**
     * A {@link VariableListener} gets notified after a source planning variable has changed.
     * That listener changes the shadow variable (often recursively on multiple planning entities) accordingly,
     * Those shadow variables should make the score calculation more natural to write.
     * <p>
     * For example: VRP with time windows uses a {@link VariableListener} to update the arrival times
     * of all the trailing entities when an entity is changed.
     *
     * @return never null (unless {@link #variableListenerRef()} is not null)
     */
    Class<? extends VariableListener> variableListenerClass() default NullVariableListener.class;

    /** Workaround for annotation limitation in {@link #variableListenerClass()}. */
    interface NullVariableListener extends VariableListener {
    }

    /**
     * The source variables (leaders) that trigger a change to this shadow variable (follower).
     *
     * @return never null (unless {@link #variableListenerRef()} is not null), at least 1
     */
    PlanningVariableReference[] sources() default {};

    /**
     * Use this when this shadow variable is updated by the {@link VariableListener} of another {@link CustomShadowVariable}.
     *
     * @return null if (and only if) any of the other fields is non null.
     */
    PlanningVariableReference variableListenerRef() default @PlanningVariableReference(variableName = "");

}
