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

package org.optaplanner.core.impl.exhaustivesearch.scope;

import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

public class ExhaustiveSearchStepScope extends AbstractStepScope {

    private final ExhaustiveSearchSolverPhaseScope phaseScope;

    private ExhaustiveSearchNode expandingNode;
    private Long selectedMoveCount = null;

    public ExhaustiveSearchStepScope(ExhaustiveSearchSolverPhaseScope phaseScope) {
        this(phaseScope, phaseScope.getNextStepIndex());
    }

    public ExhaustiveSearchStepScope(ExhaustiveSearchSolverPhaseScope phaseScope, int stepIndex) {
        super(stepIndex);
        this.phaseScope = phaseScope;
    }

    @Override
    public ExhaustiveSearchSolverPhaseScope getPhaseScope() {
        return phaseScope;
    }

    public ExhaustiveSearchNode getExpandingNode() {
        return expandingNode;
    }

    public void setExpandingNode(ExhaustiveSearchNode expandingNode) {
        this.expandingNode = expandingNode;
    }

    public Long getSelectedMoveCount() {
        return selectedMoveCount;
    }

    public void setSelectedMoveCount(Long selectedMoveCount) {
        this.selectedMoveCount = selectedMoveCount;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public Object getDepth() {
        return expandingNode.getDepth();
    }

    public long getBreadth() {
        return expandingNode.getBreadth();
    }

    @Override
    public boolean isBestSolutionCloningDelayed() {
        return true;
    }

    /**
     * Should not be called because {@link #isBestSolutionCloningDelayed} returns true
     * @return throws exception
     */
    @Override
    public int getUninitializedVariableCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNoUninitializedVariables() {
        // TODO in the deepest exhaustive steps, this is almost true
        return false;
    }

}
