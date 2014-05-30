/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
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
public @interface PlanningShadowVariable {

    /**
     * In a bidirectional relationship, the shadow side (= the slave side) uses this {@link #mappedBy()} property
     * (and nothing else) to declare for which normal {@link PlanningShadowVariable} (= the master side) it is a shadow.
     * <p/>
     * Both sides of a bidirectional relationship should be consistent: if A points to B then B must point to A.
     * When planner changes a normal variable, it adjusts the shadow variable accordingly.
     * In practice, planner ignores the shadow variables (except for consistency housekeeping).
     * @return the variable property name on the opposite end of this bidirectional relationship
     */
    String mappedBy() default "";

}
