package org.optaplanner.core.api.domain.lookup;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.impl.heuristic.move.Move;

/**
 * Specifies that a bean property (or a field) is the id to match
 * when {@link ScoreDirector#lookUpWorkingObject(Object) locating}
 * an externalObject (often from another {@link Thread} or JVM).
 * Used during {@link Move} rebasing and in a {@link ProblemChange}.
 * <p>
 * It is specified on a getter of a java bean property (or directly on a field) of a {@link PlanningEntity} class,
 * {@link ValueRangeProvider planning value} class or any {@link ProblemFactCollectionProperty problem fact} class.
 * <p>
 * The return type can be any {@link Comparable} type which overrides {@link Object#equals(Object)} and
 * {@link Object#hashCode()}, and is usually {@link Long} or {@link String}.
 * It must never return a null instance.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PlanningId {

}
