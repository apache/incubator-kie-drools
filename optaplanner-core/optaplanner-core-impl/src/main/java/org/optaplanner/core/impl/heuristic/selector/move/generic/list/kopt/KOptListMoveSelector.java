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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.Iterator;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

final class KOptListMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;

    private final EntityIndependentValueSelector<Solution_> originSelector;
    private final EntityIndependentValueSelector<Solution_> valueSelector;
    private final int minK;
    private final int maxK;

    private final int[] pickedKDistribution;

    private SingletonInverseVariableSupply inverseVariableSupply;
    private IndexVariableSupply indexVariableSupply;

    public KOptListMoveSelector(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            EntityIndependentValueSelector<Solution_> originSelector,
            EntityIndependentValueSelector<Solution_> valueSelector,
            int minK,
            int maxK,
            int[] pickedKDistribution) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.originSelector = originSelector;
        this.valueSelector = valueSelector;
        this.minK = minK;
        this.maxK = maxK;
        this.pickedKDistribution = pickedKDistribution;
        phaseLifecycleSupport.addEventListener(originSelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        inverseVariableSupply = supplyManager.demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor));
        indexVariableSupply = supplyManager.demand(new IndexVariableDemand<>(listVariableDescriptor));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
        indexVariableSupply = null;
    }

    @Override
    public long getSize() {
        long total = 0;
        long valueSelectorSize = valueSelector.getSize();
        for (int i = minK; i < Math.min(valueSelectorSize, maxK); i++) {
            if (valueSelectorSize > i) { // need more than k nodes in order to perform a k-opt
                long kOptMoveTypes = KOptUtils.getPureKOptMoveTypes(i);

                // A tour with n nodes have n - 1 edges
                // And we chose k of them to remove in a k-opt
                final long edgeChoices;
                if (valueSelectorSize <= Integer.MAX_VALUE) {
                    edgeChoices = CombinatoricsUtils.binomialCoefficient((int) (valueSelectorSize - 1), i);
                } else {
                    edgeChoices = Long.MAX_VALUE;
                }
                total += kOptMoveTypes * edgeChoices;
            }
        }
        return total;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new KOptListMoveIterator<>(workingRandom, listVariableDescriptor, inverseVariableSupply, indexVariableSupply,
                originSelector, valueSelector, minK, maxK, pickedKDistribution);
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public boolean isNeverEnding() {
        return true;
    }
}
