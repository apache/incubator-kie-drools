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

package org.optaplanner.core.api.domain.lookup;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.impl.heuristic.move.Move;

/**
 * Specifies that a bean property (or a field) is the id to match
 * when {@link ScoreDirector#lookUpWorkingObject(Object) locating}
 * an externalObject (often from another {@link Thread} or JVM).
 * Used during {@link Move} rebasing and in a {@link ProblemFactChange}.
 * <p>
 * It is specified on a getter of a java bean property (or directly on a field) of a {@link PlanningEntity} class,
 * {@link ValueRangeProvider planning value} class or any {@link ProblemFactCollectionProperty problem fact} class.
 * <p>
 * The return type can be any {@link Comparable} type which overrides {@link Object#equals(Object)} and
 * {@link Object#hashCode()}, and is usually {@link Long} or {@link String}.
 * It must never return a null instance.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PlanningId {

}
