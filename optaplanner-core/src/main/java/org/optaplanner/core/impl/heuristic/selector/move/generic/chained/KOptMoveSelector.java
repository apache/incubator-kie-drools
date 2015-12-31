/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class KOptMoveSelector extends GenericMoveSelector {

    public static final int K = 3;

    protected final EntitySelector entitySelector;
    protected final ValueSelector valueSelector;
    protected final boolean randomSelection;

    protected SingletonInverseVariableSupply inverseVariableSupply;
    protected AnchorVariableSupply anchorVariableSupply;

    public KOptMoveSelector(EntitySelector entitySelector, ValueSelector valueSelector,
            boolean randomSelection) {
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        if (!randomSelection) {
            throw new UnsupportedOperationException(
                    "Non randomSelection (such as original selection) is not yet supported on "
                    + KOptMoveSelector.class.getSimpleName() + "."); // TODO
        }
        GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
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
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        inverseVariableSupply = supplyManager.demand(new SingletonInverseVariableDemand(variableDescriptor));
        anchorVariableSupply = supplyManager.demand(new AnchorVariableDemand(variableDescriptor));
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
        anchorVariableSupply = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isCountable() {
        return entitySelector.isCountable() && valueSelector.isCountable();
    }

    public boolean isNeverEnding() {
        return randomSelection || entitySelector.isNeverEnding() || valueSelector.isNeverEnding();
    }

    public long getSize() {
        throw new UnsupportedOperationException("Not yet supported."); // TODO
//        if (valueSelector instanceof IterableSelector) {
//            return entitySelector.getSize() * (long) Math.pow(((IterableSelector) valueSelector).getSize(), K);
//        } else {
//        }
    }

    public Iterator<Move> iterator() {
        final GenuineVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        if (!randomSelection) {
            throw new UnsupportedOperationException(
                    "Non randomSelection (such as original selection) is not yet supported on "
                            + KOptMoveSelector.class.getSimpleName() + "."); // TODO
        } else {
            if (!entitySelector.isNeverEnding() || !valueSelector.isNeverEnding()) {
                throw new UnsupportedOperationException(); // TODO
            }
            final Iterator<Object> entityIterator = entitySelector.iterator();
            final Iterator<Object> valueIterator = ((EntityIndependentValueSelector) valueSelector).iterator(); // TODO
            return new UpcomingSelectionIterator<Move>() {

                @Override
                protected Move createUpcomingSelection() {
                    if (!entityIterator.hasNext()) {
                        return noUpcomingSelection();
                    }
                    Object entity = entityIterator.next();
                    if (!valueIterator.hasNext()) {
                        return noUpcomingSelection();
                    }
                    Object[] values = new Object[K - 1];
                    for (int i = 0; i < values.length; i++) {
                        values[i] = valueIterator.next();

                    }
                    return new KOptMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                            entity, values);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ", " + valueSelector + ")";
    }

}
