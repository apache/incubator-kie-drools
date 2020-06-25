/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.domain.solution;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.ProblemFactChange;

/**
 * Specifies that a property (or a field) on a {@link PlanningSolution} class is a problem fact.
 * A problem fact must not change during solving (except through a {@link ProblemFactChange} event).
 * <p>
 * The constraints in a {@link ConstraintProvider} rely on problem facts for {@link ConstraintFactory#from(Class)}.
 * Alternatively, scoreDRL relies on problem facts too.
 * <p>
 * Do not annotate a {@link PlanningEntity planning entity} or a {@link ConstraintConfiguration planning paramerization}
 * as a problem fact: they are automatically available as facts for {@link ConstraintFactory#from(Class)} or DRL.
 *
 * @see ProblemFactCollectionProperty
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ProblemFactProperty {

}
