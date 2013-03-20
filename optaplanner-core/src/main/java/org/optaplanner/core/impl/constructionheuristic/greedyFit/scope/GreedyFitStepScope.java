/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.constructionheuristic.greedyFit.scope;

import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;

public class GreedyFitStepScope extends AbstractStepScope {

    private final GreedyFitSolverPhaseScope phaseScope;

    private Object planningEntity;

    private Move step = null;
    private String stepString = null;
    private Move undoStep = null;

    public GreedyFitStepScope(GreedyFitSolverPhaseScope phaseScope) {
        this.phaseScope = phaseScope;
    }

    @Override
    public GreedyFitSolverPhaseScope getPhaseScope() {
        return phaseScope;
    }

    @Override
    public boolean isBestSolutionCloningDelayed() {
        return true;
    }

    /**
     * Should not be called because {@link #isBestSolutionCloningDelayed} return true
     * @return throws exception
     */
    @Override
    public int getUninitializedVariableCount() {
        throw new UnsupportedOperationException();
    }

    public Object getPlanningEntity() {
        return planningEntity;
    }

    public void setPlanningEntity(Object planningEntity) {
        this.planningEntity = planningEntity;
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

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}
