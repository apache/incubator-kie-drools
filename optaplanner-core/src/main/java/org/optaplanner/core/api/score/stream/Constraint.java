/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream;

import java.util.function.Predicate;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public interface Constraint {

    /**
     * Start a {@link ConstraintStream} of all instances of the fromClass
     * that are known as {@link ProblemFactCollectionProperty problem facts} or {@link PlanningEntity planning entities}.
     * <p>
     * If the fromClass is a {@link PlanningEntity}, then it will be automatically
     * {@link UniConstraintStream#filter(Predicate) filtered} to only contain fully initialized entities,
     * for which each genuine {@link PlanningVariable} (of the fromClass or a superclass thereof) is initialized
     * (so not null unless {@link PlanningVariable#nullable()} is modified).
     * This filtering will NOT automatically apply to genuine planning variables of subclass planning entities of fromClass.
     * @param fromClass never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return never null
     */
    <A> UniConstraintStream<A> from(Class<A> fromClass);

    /**
     * Like {@link #from(Class)},
     * but without any filtering of initialized {@link PlanningEntity planning entities}.
     * @param fromClass never null
     * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
     * @return never null
     */
    <A> UniConstraintStream<A> fromUnfiltered(Class<A> fromClass);

}
