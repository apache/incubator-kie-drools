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

package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public final class EntityIndependentFilteringValueSelector<Solution_>
        extends FilteringValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    public EntityIndependentFilteringValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector,
            List<SelectionFilter<Solution_, Object>> filterList) {
        super(childValueSelector, filterList);
    }

    @Override
    public long getSize() {
        return ((EntityIndependentValueSelector<Solution_>) childValueSelector).getSize();
    }

    @Override
    public Iterator<Object> iterator() {
        return new JustInTimeFilteringValueIterator(((EntityIndependentValueSelector<Solution_>) childValueSelector).iterator(),
                determineBailOutSize());
    }

    protected long determineBailOutSize() {
        if (!bailOutEnabled) {
            return -1L;
        }
        return ((EntityIndependentValueSelector<Solution_>) childValueSelector).getSize() * 10L;
    }

}
