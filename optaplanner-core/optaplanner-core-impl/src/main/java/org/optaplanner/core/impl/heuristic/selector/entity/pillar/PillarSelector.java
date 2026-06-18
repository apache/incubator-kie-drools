/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.entity.pillar;

import java.util.List;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.ListIterableSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

/**
 * A pillar is a {@link List} of entities that have the same planning value for each (or a subset)
 * of their planning values.
 * Selects a {@link List} of such entities that are moved together.
 *
 * @see EntitySelector
 */
public interface PillarSelector<Solution_> extends ListIterableSelector<Solution_, List<Object>> {

    /**
     * @return never null
     */
    EntityDescriptor<Solution_> getEntityDescriptor();

}
