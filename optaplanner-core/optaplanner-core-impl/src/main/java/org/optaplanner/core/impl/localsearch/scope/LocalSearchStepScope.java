/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class LocalSearchStepScope<Solution_> extends AbstractStepScope<Solution_> {

    private final LocalSearchPhaseScope<Solution_> phaseScope;

    private double timeGradient = Double.NaN;
    private Move<Solution_> step = null;
    private String stepString = null;
    private Move<Solution_> undoStep = null;
    private Long selectedMoveCount = null;
    private Long acceptedMoveCount = null;

    public LocalSearchStepScope(LocalSearchPhaseScope<Solution_> phaseScope) {
        this(phaseScope, phaseScope.getNextStepIndex());
    }

    public LocalSearchStepScope(LocalSearchPhaseScope<Solution_> phaseScope, int stepIndex) {
        super(stepIndex);
        this.phaseScope = phaseScope;
    }

    @Override
    public LocalSearchPhaseScope<Solution_> getPhaseScope() {
        return phaseScope;
    }

    public double getTimeGradient() {
        return timeGradient;
    }

    public void setTimeGradient(double timeGradient) {
        this.timeGradient = timeGradient;
    }

    public Move<Solution_> getStep() {
        return step;
    }

    public void setStep(Move<Solution_> step) {
        this.step = step;
    }

    /**
     * @return null if logging level is too high
     */
    public String getStepString() {
        return stepString;
    }

    public void setStepString(String stepString) {
        this.stepString = stepString;
    }

    public Move<Solution_> getUndoStep() {
        return undoStep;
    }

    public void setUndoStep(Move<Solution_> undoStep) {
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
