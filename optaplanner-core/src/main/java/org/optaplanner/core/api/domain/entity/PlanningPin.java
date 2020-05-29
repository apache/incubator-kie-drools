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

package org.optaplanner.core.api.domain.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a boolean property (or field) of a {@link PlanningEntity} determines if the planning entity is pinned.
 * A pinned planning entity is never changed during planning.
 * For example, it allows the user to pin a shift to a specific employee before solving
 * and the solver will not undo that, regardless of the constraints.
 * <p>
 * The boolean is false if the planning entity is movable and true if the planning entity is pinned.
 * <p>
 * It applies to all the planning variables of that planning entity.
 * To make individual variables pinned, see https://issues.redhat.com/browse/PLANNER-124
 * <p>
 * This is syntactic sugar for {@link PlanningEntity#pinningFilter()},
 * which is a more flexible and verbose way to pin a planning entity.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PlanningPin {

}
