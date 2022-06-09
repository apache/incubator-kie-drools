package org.optaplanner.core.api.domain.solution;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class is a planning entity.
 * <p>
 * The planning entity should have the {@link PlanningEntity} annotation.
 * The planning entity will be added to the {@link ScoreDirector}.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PlanningEntityProperty {

}
