/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.api.domain.variable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Comparator;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.core.solution.Solution;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that a bean property should be kept up-to-date by Drools Planner
 * when the master {@link PlanningVariable} changes.
 * <p/>
 * It is specified on a getter of a java bean property of a {@link PlanningEntity} class.
 * @see PlanningVariable
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface DependentPlanningVariable {

    /**
     * The property <b>on this class</b> that is the master variable.
     * When the master variable changes, this variable must to be kept up-to-date.
     * <p/>
     * When Drools Planner changes the master variable, it will automatically update this variable.
     * However, when other code changes the master variable, it must also update this variable.
     * @return never null
     */
    String master();

    /**
     * The property <b>on the opposite class</b> that owns the relationship.
     * The opposite class is the class returned by this property.
     * </p>
     * When the relationship is bidirectional, it is required.
     * <p/>
     * Often, it holds the same value as {@link #master()},
     * especially when the opposite class is the same class as this class.
     * @return "" if unused
     */
    String mappedBy() default "";
    
}
