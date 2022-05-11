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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableDemand;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalChangeIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomChangeIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Also known as a 2-opt move selector.
 */
public class TailChainSwapMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    protected final EntitySelector<Solution_> entitySelector;
    protected final ValueSelector<Solution_> valueSelector;
    protected final boolean randomSelection;

    protected SingletonInverseVariableSupply inverseVariableSupply;
    protected AnchorVariableSupply anchorVariableSupply;

    public TailChainSwapMoveSelector(EntitySelector<Solution_> entitySelector, ValueSelector<Solution_> valueSelector,
            boolean randomSelection) {
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        if (!variableDescriptor.isChained()) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s valueSelector's  variableDescriptor (" + variableDescriptor
                    + ") must be chained (" + variableDescriptor.isChained() + ").");
        }
        if (!variableDescriptor.getEntityDescriptor().getEntityClass().isAssignableFrom(
                entitySelector.getEntityDescriptor().getEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a valueSelector with a entityClass ("
                    + variableDescriptor.getEntityDescriptor().getEntityClass()
                    + ") which is not equal or a superclass to the entitySelector's entityClass ("
                    + entitySelector.getEntityDescriptor().getEntityClass() + ").");
        }
        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        inverseVariableSupply = supplyManager.demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        anchorVariableSupply = supplyManager.demand(new AnchorVariableDemand<>(variableDescriptor));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
        anchorVariableSupply = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return entitySelector.isCountable() && valueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || entitySelector.isNeverEnding() || valueSelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        if (valueSelector instanceof IterableSelector) {
            return entitySelector.getSize() * ((IterableSelector<Solution_, ?>) valueSelector).getSize();
        } else {
            long size = 0;
            for (Iterator<?> it = entitySelector.endingIterator(); it.hasNext();) {
                Object entity = it.next();
                size += valueSelector.getSize(entity);
            }
            return size;
        }
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        final GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        if (!randomSelection) {
            return new AbstractOriginalChangeIterator<>(entitySelector, valueSelector) {
                @Override
                protected Move<Solution_> newChangeSelection(Object entity, Object toValue) {
                    return new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                            entity, toValue);
                }
            };
        } else {
            return new AbstractRandomChangeIterator<>(entitySelector, valueSelector) {
                @Override
                protected Move<Solution_> newChangeSelection(Object entity, Object toValue) {
                    return new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                            entity, toValue);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ", " + valueSelector + ")";
    }

}
