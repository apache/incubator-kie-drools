package org.optaplanner.core.api.domain.solution;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class is a {@link Collection} of planning entities.
 * <p>
 * Every element in the planning entity collection should have the {@link PlanningEntity} annotation.
 * Every element in the planning entity collection will be added to the {@link ScoreDirector}.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PlanningEntityCollectionProperty {

}
