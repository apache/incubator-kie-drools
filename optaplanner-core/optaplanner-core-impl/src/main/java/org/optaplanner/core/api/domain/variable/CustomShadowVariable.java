package org.optaplanner.core.api.domain.variable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

/**
 * This annotation is deprecated. Below are the instructions on how to replace your {@code @CustomShadowVariable(...)}
 * with either {@link ShadowVariable @ShadowVariable} or {@link PiggybackShadowVariable @PiggybackShadowVariable}.
 * <p>
 * If your {@code @CustomShadowVariable} uses the {@code variableListenerClass} attribute, then replace the annotation with one
 * {@code @ShadowVariable} annotation for each source {@code @PlanningVariableReference}.
 * <p>
 * For example,
 *
 * <pre>
 * &#64;CustomShadowVariable(
 *     variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class,
 *     sources = {
 *         &#64;PlanningVariableReference(variableName = "executionMode"),
 *         &#64;PlanningVariableReference(variableName = "delay") })
 * </pre>
 *
 * becomes:
 *
 * <pre>
 * &#64;ShadowVariable(
 *     variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class,
 *     sourceVariableName = "executionMode")
 * &#64;ShadowVariable(
 *     variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class,
 *     sourceVariableName = "delay")
 * </pre>
 * <p>
 * If your {@code @CustomShadowVariable} uses the {@code variableListenerRef} attribute, then replace it with the
 * {@code @PiggybackShadowVariable} annotation.
 * <p>
 * For example,
 *
 * <pre>
 * &#64;CustomShadowVariable(
 *     variableListenerRef = @PlanningVariableReference(variableName = "date"))
 * </pre>
 *
 * becomes:
 *
 * <pre>
 * &#64;PiggybackShadowVariable(shadowVariableName = "date")
 * </pre>
 *
 * Specifies that a bean property (or a field) is a custom shadow variable of 1 or more {@link PlanningVariable}s.
 * <p>
 * It is specified on a getter of a java bean property (or a field) of a {@link PlanningEntity} class.
 *
 * @deprecated Deprecated in favor of {@link ShadowVariable} (normal shadow variable with {@link #variableListenerClass()})
 *             and {@link PiggybackShadowVariable} (if {@link #variableListenerRef()} is used).
 */
@Deprecated(forRemoval = true)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface CustomShadowVariable {

    /**
     * A {@link VariableListener} gets notified after a source planning variable has changed.
     * That listener changes the shadow variable (often recursively on multiple planning entities) accordingly.
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
