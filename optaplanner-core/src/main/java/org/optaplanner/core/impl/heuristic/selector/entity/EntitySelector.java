/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.entity;

import java.util.Iterator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.ListIterableSelector;

/**
 * Selects instances of 1 {@link PlanningEntity} annotated class.
 * @see AbstractEntitySelector
 * @see FromSolutionEntitySelector
 */
public interface EntitySelector extends ListIterableSelector<Object> {

    /**
     * @return never null
     */
    EntityDescriptor getEntityDescriptor();

    /**
     * If {@link #isNeverEnding()} is true, then {@link #iterator()} will never end.
     * This returns an ending {@link Iterator}, that tries to match {@link #iterator()} as much as possible,
     * but returns each distinct element only once and returns every element that might possibly be selected
     * and therefore it might not respect the configuration of this {@link EntitySelector} entirely.
     * @return never null
     * @see #iterator()
     */
    Iterator<Object> endingIterator();

}
