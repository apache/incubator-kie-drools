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

package org.drools.planner.core.heuristic.selector.entity;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.Selector;

/**
 * Selects instances of 1 {@link PlanningEntity} annotated class.
 * @see AbstractEntitySelector
 * @see FromSolutionEntitySelector
 */
public interface EntitySelector extends Selector, Iterable<Object> {

    /**
     * @return never null
     */
    PlanningEntityDescriptor getEntityDescriptor();

    /**
     * See {@link List#listIterator()}
     *
     * @return never null, see {@link List#listIterator()}.
     */
    ListIterator<Object> listIterator();

    /**
     * See {@link List#listIterator()}
     *
     * @param index lower than {@link #getSize()}, see {@link List#listIterator(int)}.
     * @return never null, see {@link List#listIterator(int)}.
     */
    ListIterator<Object> listIterator(int index);

}
