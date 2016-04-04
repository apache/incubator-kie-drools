/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class holds the {@link Score} of that solution.
 * <p>
 * This property can be null if the {@link PlanningSolution} is uninitialized.
 * <p>
 * This property is modified by the {@link Solver},
 * every time when the {@link Score} of this {@link PlanningSolution} has been calculated.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface PlanningScore {

}
