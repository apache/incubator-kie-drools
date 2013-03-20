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

package org.optaplanner.core.api.domain.solution;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.solution.Solution;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that the class is a planning solution.
 * Each planning solution must have at least 1 {@link PlanningEntityCollectionProperty}
 * or {@link PlanningEntityProperty} property.
 * <p/>
 * The class should have a public no-arg constructor, so it can be cloned.
 * TODO currently this violated DRY because the user needs to implement {@link Solution} too.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface PlanningSolution {

    /**
     * Overrides the default {@link SolutionCloner} to implement a custom {@link Solution} cloning implementation.
     * <p/>
     * If this is not specified and the {@link Solution} does not implements {@link PlanningCloneable},
     * the default reflection-based {@link SolutionCloner} is used, so you don't have to worry about it.
     * @return {@link NullSolutionCloner} when it is null (workaround for annotation limitation)
     */
    Class<? extends SolutionCloner> solutionCloner()
            default NullSolutionCloner.class;

    interface NullSolutionCloner extends SolutionCloner {}

}
