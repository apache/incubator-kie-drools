package org.optaplanner.core.api.domain.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a boolean property (or field) of a {@link PlanningEntity} determines if the planning entity is pinned.
 * A pinned planning entity is never changed during planning.
 * For example, it allows the user to pin a shift to a specific employee before solving
 * and the solver will not undo that, regardless of the constraints.
 * <p>
 * The boolean is false if the planning entity is movable and true if the planning entity is pinned.
 * <p>
 * It applies to all the planning variables of that planning entity.
 * To make individual variables pinned, see https://issues.redhat.com/browse/PLANNER-124
 * <p>
 * This is syntactic sugar for {@link PlanningEntity#pinningFilter()},
 * which is a more flexible and verbose way to pin a planning entity.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PlanningPin {

}
