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
//    private Long selectedMoveCount = null;

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

//
//
//    public Object getEntity() {
//        return entity;
//    }
//
//    public void setEntity(Object entity) {
//        this.entity = entity;
//    }
//
//    public Move getStep() {
//        return step;
//    }
//
//    public void setStep(Move step) {
//        this.step = step;
//    }
//
//    /**
//     * @return null if logging level is to high
//     */
//    public String getStepString() {
//        return stepString;
//    }
//
//    public void setStepString(String stepString) {
//        this.stepString = stepString;
//    }
//
//    public Move getUndoStep() {
//        return undoStep;
//    }
//
//    public void setUndoStep(Move undoStep) {
//        this.undoStep = undoStep;
//    }
//
//    public Long getSelectedMoveCount() {
//        return selectedMoveCount;
//    }
//
//    public void setSelectedMoveCount(Long selectedMoveCount) {
//        this.selectedMoveCount = selectedMoveCount;
//    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public Object getDepth() {
        return expandingNode.getDepth();
    }

    @Override
    public boolean isBestSolutionCloningDelayed() { // TODO
        return true;
    }

    /**
     * Should not be called because {@link #isBestSolutionCloningDelayed} returns true
     * @return throws exception
     */
    @Override
    public int getUninitializedVariableCount() { // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNoUninitializedVariables() { // TODO
        // TODO might be true in the last step of a construction heuristic
        return false;
    }
//    @Override
//    public boolean hasNoUninitializedVariables() {
//        // TODO might be true in the last step of a construction heuristic
//        return false;
//    }

}
