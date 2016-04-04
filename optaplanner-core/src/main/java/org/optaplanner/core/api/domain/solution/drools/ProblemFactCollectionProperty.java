/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.domain.solution.drools;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class is a {@link Collection} of problem facts.
 * A problem fact must not change during solving (except through {@link ProblemFactChange} event).
 * <p>
 * The problem facts will be added as facts in the {@link KieSession} of the {@link DroolsScoreDirector},
 * so the score rules can use them.
 * <p>
 * Do not annotate {@link PlanningEntity planning entities} as problem facts:
 * they are automatically inserted into the {@link KieSession}.
 * @see ProblemFactProperty
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ProblemFactCollectionProperty {

}
