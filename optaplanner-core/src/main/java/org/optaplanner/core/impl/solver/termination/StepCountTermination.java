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

package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class StepCountTermination extends AbstractTermination {

    private final int stepCountLimit;

    public StepCountTermination(int stepCountLimit) {
        this.stepCountLimit = stepCountLimit;
        if (stepCountLimit < 0) {
            throw new IllegalArgumentException("The stepCountLimit (" + stepCountLimit
                    + ") cannot be negative.");
        }
    }

    public int getStepCountLimit() {
        return stepCountLimit;
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " can only be used for phase termination.");
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        int nextStepIndex = phaseScope.getNextStepIndex();
        return nextStepIndex >= stepCountLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " can only be used for phase termination.");
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        int nextStepIndex = phaseScope.getNextStepIndex();
        double timeGradient = ((double) nextStepIndex) / ((double) stepCountLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public StepCountTermination createChildThreadTermination(
            DefaultSolverScope solverScope, ChildThreadType childThreadType) {
        return new StepCountTermination(stepCountLimit);
    }

    @Override
    public String toString() {
        return "StepCount(" + stepCountLimit + ")";
    }

}
