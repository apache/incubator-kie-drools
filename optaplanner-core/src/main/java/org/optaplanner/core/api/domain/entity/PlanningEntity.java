/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.domain.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that the class is a planning entity.
 * Each planning entity must have at least 1 {@link PlanningVariable} property.
 * <p>
 * The class should have a public no-arg constructor, so it can be cloned
 * (unless the {@link PlanningSolution#solutionCloner()} is specified).
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface PlanningEntity {

    /**
     * An immovable planning entity is never changed during planning,
     * this is useful in repeated planning use cases (such as continuous planning and real-time planning).
     * <p>
     * This applies to all the planning variables of this planning entity.
     * To make individual variables immovable, see https://issues.jboss.org/browse/PLANNER-124
     * <p>
     * The method {@link SelectionFilter#accept(ScoreDirector, Object)} returns false
     * if the selection entity is immovable and it returns true if the selection entity is movable
     * @return {@link NullMovableEntitySelectionFilter} when it is null (workaround for annotation limitation)
     */
    Class<? extends SelectionFilter> movableEntitySelectionFilter()
            default NullMovableEntitySelectionFilter.class;

    /** Workaround for annotation limitation in {@link #movableEntitySelectionFilter()}. */
    interface NullMovableEntitySelectionFilter extends SelectionFilter {}

    /**
     * Allows a collection of planning entities to be sorted by difficulty.
     * A difficultyWeight estimates how hard is to plan a certain PlanningEntity.
     * Some algorithms benefit from planning on more difficult planning entities first/last or from focusing on them.
     * <p>
     * The {@link Comparator} should sort in ascending difficulty
     * (even though many optimization algorithms will reverse it).
     * For example: sorting 3 processes on difficultly based on their RAM usage requirement:
     * Process B (1GB RAM), Process A (2GB RAM), Process C (7GB RAM),
     * <p>
     * Do not use together with {@link #difficultyWeightFactoryClass()}.
     * @return {@link NullDifficultyComparator} when it is null (workaround for annotation limitation)
     * @see #difficultyWeightFactoryClass()
     */
    Class<? extends Comparator> difficultyComparatorClass() default NullDifficultyComparator.class;

    /** Workaround for annotation limitation in {@link #difficultyComparatorClass()}. */
    interface NullDifficultyComparator extends Comparator {}

    /**
     * The {@link SelectionSorterWeightFactory} alternative for {@link #difficultyComparatorClass()}.
     * <p>
     * Do not use together with {@link #difficultyComparatorClass()}.
     * @return {@link NullDifficultyWeightFactory} when it is null (workaround for annotation limitation)
     * @see #difficultyComparatorClass()
     */
    Class<? extends SelectionSorterWeightFactory> difficultyWeightFactoryClass()
            default NullDifficultyWeightFactory.class;

    /** Workaround for annotation limitation in {@link #difficultyWeightFactoryClass()}. */
    interface NullDifficultyWeightFactory extends SelectionSorterWeightFactory {}

}
