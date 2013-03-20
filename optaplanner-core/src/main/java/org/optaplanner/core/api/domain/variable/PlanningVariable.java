/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.domain.variable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that a bean property can be changed and should be optimized by the optimization algorithms.
 * <p/>
 * It is specified on a getter of a java bean property of a {@link PlanningEntity} class.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface PlanningVariable {

    /**
     * A nullable planning variable will automatically add the planning value null to the {@link ValueRange}.
     * <p/>
     * In repeated planning use cases, it's recommended to specify a {@link #reinitializeVariableEntityFilter()}
     * for every nullable planning variable too.
     * <p/>
     * {@link #nullable()} true is not compatible with {#link #chained} true.
     * {@link #nullable()} true is not compatible with a primitive property type.
     * @return true if null is a valid value for this planning variable
     */
    boolean nullable() default false;

    /**
     * Construction heuristics only change reinitializable planning variables.
     * Non reinitializable planning variable is ignored by construction heuristics.
     * This is especially useful in repeated planning use cases,
     * in which starting from scratch would waste previous results and time.
     * <p/>
     * If no {@link #reinitializeVariableEntityFilter} is specified,
     * the default considers an entity uninitialized for a variable if its value is null
     * (even if {@link #nullable()} is true).
     * <p/>
     * The method {@link SelectionFilter#accept(ScoreDirector, Object)}
     * returns false if the selection entity should be reinitialized for this variable
     * and it returns true if the selection entity should not be reinitialized for this variable
     * @return {@link NullReinitializeVariableEntityFilter} when it is null (workaround for annotation limitation)
     */
    Class<? extends SelectionFilter> reinitializeVariableEntityFilter()
            default NullReinitializeVariableEntityFilter.class;

    interface NullReinitializeVariableEntityFilter extends SelectionFilter {}

    /**
     * Allows a collection of planning values for this variable to be sorted by strength.
     * A strengthWeight estimates how strong a planning value is.
     * Some algorithms benefit from planning on weaker planning values first or from focusing on them.
     * <p/>
     * The {@link Comparator} should sort in ascending strength.
     * For example: sorting 3 computers on strength based on their RAM capacity:
     * Computer B (1GB RAM), Computer A (2GB RAM), Computer C (7GB RAM),
     * <p/>
     * Do not use together with {@link #strengthWeightFactoryClass()}.
     * @return {@link NullStrengthComparator} when it is null (workaround for annotation limitation)
     * @see #strengthWeightFactoryClass()
     */
    Class<? extends Comparator> strengthComparatorClass()
            default NullStrengthComparator.class;

    interface NullStrengthComparator extends Comparator {}

    /**
     * The {@link SelectionSorterWeightFactory} alternative for {@link #strengthComparatorClass()}.
     * <p/>
     * Do not use together with {@link #strengthComparatorClass()}.
     * @return {@link NullStrengthWeightFactory} when it is null (workaround for annotation limitation)
     * @see #strengthComparatorClass()
     */
    Class<? extends SelectionSorterWeightFactory> strengthWeightFactoryClass()
            default NullStrengthWeightFactory.class;

    interface NullStrengthWeightFactory extends SelectionSorterWeightFactory {}

    /**
     * In some use cases, such as Vehicle Routing, planning entities are chained.
     * A chained variable recursively points to a planning fact, which is called the anchor.
     * So either it points directly to the anchor (that planning fact)
     * or it points to another planning entity with the same planning variable (which recursively points to the anchor).
     * Chains always have exactly 1 anchor, thus they never loop and the tail is always open.
     * Chains never split into a tree: a anchor or planning entity has at most 1 trailing planning entity.
     * <p/>
     * When a chained planning entity changes position, then chain correction must happen:
     * <ul>
     *     <li>divert the chain link at the new position to go through the modified planning entity</li>
     *     <li>close the missing chain link at the old position</li>
     * </ul>
     * For example: Given A <- B <- C <- D <- X <- Y, when B moves between X and Y, pointing to X,
     * then Y is also changed to point to B
     * and C is also changed to point to A,
     * giving the result A <- C <- D <- X <- B <- Y.
     * <p/>
     * {@link #nullable()} true is not compatible with {#link #chained} true.
     * @return true if changes to this variable need to trigger chain correction
     */
    boolean chained() default false;

}
