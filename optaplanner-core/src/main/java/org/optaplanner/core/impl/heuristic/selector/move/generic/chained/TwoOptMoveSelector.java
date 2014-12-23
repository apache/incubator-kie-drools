/*
 * Copyright 2014 JBoss Inc
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

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableDemand;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class TwoOptMoveSelector extends GenericMoveSelector {

    protected final EntitySelector leftEntitySelector;
    protected final EntitySelector rightEntitySelector;
    protected final GenuineVariableDescriptor variableDescriptor;
    protected final boolean randomSelection;

    protected AnchorVariableSupply anchorVariableSupply;

    public TwoOptMoveSelector(EntitySelector leftEntitySelector, EntitySelector rightEntitySelector,
            GenuineVariableDescriptor variableDescriptor, boolean randomSelection) {
        this.leftEntitySelector = leftEntitySelector;
        this.rightEntitySelector = rightEntitySelector;
        this.variableDescriptor = variableDescriptor;
        this.randomSelection = randomSelection;
        EntityDescriptor leftEntityDescriptor = leftEntitySelector.getEntityDescriptor();
        EntityDescriptor rightEntityDescriptor = rightEntitySelector.getEntityDescriptor();
        if (!leftEntityDescriptor.getEntityClass().equals(rightEntityDescriptor.getEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a leftEntitySelector's entityClass (" + leftEntityDescriptor.getEntityClass()
                    + ") which is not equal to the rightEntitySelector's entityClass ("
                    + rightEntityDescriptor.getEntityClass() + ").");
        }
        if (!variableDescriptor.isChained()) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s variableDescriptor (" + variableDescriptor
                    + ") must be chained (" + variableDescriptor.isChained() + ").");
        }
        if (!variableDescriptor.getEntityDescriptor().getEntityClass().isAssignableFrom(
                leftEntityDescriptor.getEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a variableDescriptor with a entityClass ("
                    + variableDescriptor.getEntityDescriptor().getEntityClass()
                    + ") which is not equal or a superclass to the leftEntitySelector's entityClass ("
                    + leftEntityDescriptor.getEntityClass() + ").");
        }
        phaseLifecycleSupport.addEventListener(leftEntitySelector);
        if (leftEntitySelector != rightEntitySelector) {
            phaseLifecycleSupport.addEventListener(rightEntitySelector);
        }
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        // TODO supply is demanded just to make sure it's there when it's demand again later.
        // Instead it should be remembered for later
        anchorVariableSupply = supplyManager.demand(
                new AnchorVariableDemand(variableDescriptor));
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        anchorVariableSupply = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isCountable() {
        return leftEntitySelector.isCountable() && rightEntitySelector.isCountable();
    }

    public boolean isNeverEnding() {
        return randomSelection || leftEntitySelector.isNeverEnding() || rightEntitySelector.isNeverEnding();
    }

    public long getSize() {
        return AbstractOriginalSwapIterator.getSize(leftEntitySelector, rightEntitySelector);
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<Move, Object>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return new TwoOptMove(variableDescriptor, anchorVariableSupply, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<Move, Object>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return new TwoOptMove(variableDescriptor, anchorVariableSupply, leftSubSelection, rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftEntitySelector + ", " + rightEntitySelector + ")";
    }

}
