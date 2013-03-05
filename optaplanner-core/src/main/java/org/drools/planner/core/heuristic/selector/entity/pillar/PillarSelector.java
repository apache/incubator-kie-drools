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

package org.drools.planner.core.heuristic.selector.entity.pillar;

import java.util.List;
import java.util.ListIterator;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.IterableSelector;
import org.drools.planner.core.heuristic.selector.ListIterableSelector;
import org.drools.planner.core.heuristic.selector.common.iterator.ListIterable;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;

/**
 * A pillar is a {@link List} of entities that are somehow related.
 * Selects a {@link List} of somehow related entities that are moved together.
 * @see EntitySelector
 */
public interface PillarSelector extends ListIterableSelector<List<Object>> {

    /**
     * @return never null
     */
    PlanningEntityDescriptor getEntityDescriptor();

}
