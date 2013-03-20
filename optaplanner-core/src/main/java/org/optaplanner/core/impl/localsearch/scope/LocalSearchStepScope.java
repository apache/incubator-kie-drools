/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.localsearch.scope;

import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;

public class LocalSearchStepScope extends AbstractStepScope {

    private final LocalSearchSolverPhaseScope phaseScope;

    private double timeGradient = Double.NaN;
    private Move step = null;
    private String stepString = null;
    private Move undoStep = null;
    private Long selectedMoveCount = null;
    private Long acceptedMoveCount = null;

    public LocalSearchStepScope(LocalSearchSolverPhaseScope phaseScope) {
        this.phaseScope = phaseScope;
    }

    @Override
    public LocalSearchSolverPhaseScope getPhaseScope() {
        return phaseScope;
    }

    @Override
    public boolean isBestSolutionCloningDelayed() {
        return false;
    }

    @Override
    public int getUninitializedVariableCount() {
        return 0;
    }

    public double getTimeGradient() {
        return timeGradient;
    }

    public void setTimeGradient(double timeGradient) {
        this.timeGradient = timeGradient;
    }

    public Move getStep() {
        return step;
    }

    public void setStep(Move step) {
        this.step = step;
    }

    /**
     * @return null if logging level is to high
     */
    public String getStepString() {
        return stepString;
    }

    public void setStepString(String stepString) {
        this.stepString = stepString;
    }

    public Move getUndoStep() {
        return undoStep;
    }

    public void setUndoStep(Move undoStep) {
        this.undoStep = undoStep;
    }

    public Long getSelectedMoveCount() {
        return selectedMoveCount;
    }

    public void setSelectedMoveCount(Long selectedMoveCount) {
        this.selectedMoveCount = selectedMoveCount;
    }

    public Long getAcceptedMoveCount() {
        return acceptedMoveCount;
    }

    public void setAcceptedMoveCount(Long acceptedMoveCount) {
        this.acceptedMoveCount = acceptedMoveCount;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}
