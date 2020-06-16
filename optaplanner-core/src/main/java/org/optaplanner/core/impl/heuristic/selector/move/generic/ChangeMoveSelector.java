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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalChangeIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomChangeIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class ChangeMoveSelector extends GenericMoveSelector {

    protected final EntitySelector entitySelector;
    protected final ValueSelector valueSelector;
    protected final boolean randomSelection;

    protected final boolean chained;
    protected SingletonInverseVariableSupply inverseVariableSupply = null;

    public ChangeMoveSelector(EntitySelector entitySelector, ValueSelector valueSelector,
            boolean randomSelection) {
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        chained = variableDescriptor.isChained();
        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    @Override
    public boolean supportsPhaseAndSolverCaching() {
        return !chained;
    }

    @Override
    public void solvingStarted(SolverScope solverScope) {
        super.solvingStarted(solverScope);
        if (chained) {
            SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
            inverseVariableSupply = supplyManager.demand(
                    new SingletonInverseVariableDemand(valueSelector.getVariableDescriptor()));
        }
    }

    @Override
    public void solvingEnded(SolverScope solverScope) {
        super.solvingEnded(solverScope);
        if (chained) {
            inverseVariableSupply = null;
        }
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
            return entitySelector.getSize() * ((IterableSelector) valueSelector).getSize();
        } else {
            long size = 0;
            for (Iterator it = entitySelector.endingIterator(); it.hasNext();) {
                Object entity = it.next();
                size += valueSelector.getSize(entity);
            }
            return size;
        }
    }

    @Override
    public Iterator<Move> iterator() {
        final GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        if (!randomSelection) {
            if (chained) {
                return new AbstractOriginalChangeIterator<Move>(entitySelector, valueSelector) {
                    @Override
                    protected Move newChangeSelection(Object entity, Object toValue) {
                        return new ChainedChangeMove(entity, variableDescriptor, inverseVariableSupply, toValue);
                    }
                };
            } else {
                return new AbstractOriginalChangeIterator<Move>(entitySelector, valueSelector) {
                    @Override
                    protected Move newChangeSelection(Object entity, Object toValue) {
                        return new ChangeMove(entity, variableDescriptor, toValue);
                    }
                };
            }
        } else {
            if (chained) {
                return new AbstractRandomChangeIterator<Move>(entitySelector, valueSelector) {
                    @Override
                    protected Move newChangeSelection(Object entity, Object toValue) {
                        return new ChainedChangeMove(entity, variableDescriptor, inverseVariableSupply, toValue);
                    }
                };
            } else {
                return new AbstractRandomChangeIterator<Move>(entitySelector, valueSelector) {
                    @Override
                    protected Move newChangeSelection(Object entity, Object toValue) {
                        return new ChangeMove(entity, variableDescriptor, toValue);
                    }
                };
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ", " + valueSelector + ")";
    }

}
