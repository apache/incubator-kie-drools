package org.optaplanner.core.api.domain.solution;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.change.ProblemChange;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class is a {@link Collection} of problem facts.
 * A problem fact must not change during solving (except through a {@link ProblemChange} event).
 * <p>
 * The constraints in a {@link ConstraintProvider} rely on problem facts for {@link ConstraintFactory#forEach(Class)}.
 * Alternatively, scoreDRL relies on problem facts too.
 * <p>
 * Do not annotate {@link PlanningEntity planning entities} as problem facts:
 * they are automatically available as facts for {@link ConstraintFactory#forEach(Class)} or DRL.
 *
 * @see ProblemFactProperty
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ProblemFactCollectionProperty {

}
