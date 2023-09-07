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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class RandomListSwapIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final SingletonInverseVariableSupply inverseVariableSupply;
    private final IndexVariableSupply indexVariableSupply;
    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final Iterator<Object> leftValueIterator;
    private final Iterator<Object> rightValueIterator;

    public RandomListSwapIterator(
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply,
            EntityIndependentValueSelector<Solution_> leftValueSelector,
            EntityIndependentValueSelector<Solution_> rightValueSelector) {
        this.inverseVariableSupply = inverseVariableSupply;
        this.indexVariableSupply = indexVariableSupply;
        this.listVariableDescriptor = (ListVariableDescriptor<Solution_>) leftValueSelector.getVariableDescriptor();
        this.leftValueIterator = leftValueSelector.iterator();
        this.rightValueIterator = rightValueSelector.iterator();
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        if (!leftValueIterator.hasNext() || !rightValueIterator.hasNext()) {
            return noUpcomingSelection();
        }

        Object upcomingLeftValue = leftValueIterator.next();
        Object upcomingRightValue = rightValueIterator.next();

        return new ListSwapMove<>(
                listVariableDescriptor,
                inverseVariableSupply.getInverseSingleton(upcomingLeftValue),
                indexVariableSupply.getIndex(upcomingLeftValue),
                inverseVariableSupply.getInverseSingleton(upcomingRightValue),
                indexVariableSupply.getIndex(upcomingRightValue));
    }
}
