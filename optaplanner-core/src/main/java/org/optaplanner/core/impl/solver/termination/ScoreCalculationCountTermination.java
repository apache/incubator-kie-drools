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

package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class ScoreCalculationCountTermination extends AbstractTermination {

    private final long scoreCalculationCountLimit;

    public ScoreCalculationCountTermination(long scoreCalculationCountLimit) {
        this.scoreCalculationCountLimit = scoreCalculationCountLimit;
        if (scoreCalculationCountLimit < 0L) {
            throw new IllegalArgumentException("The scoreCalculationCountLimit (" + scoreCalculationCountLimit
                    + ") cannot be negative.");
        }
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        return isTerminated(solverScope.getScoreDirector());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        return isTerminated(phaseScope.getScoreDirector());
    }

    protected boolean isTerminated(InnerScoreDirector scoreDirector) {
        long scoreCalculationCount = scoreDirector.getCalculationCount();
        return scoreCalculationCount >= scoreCalculationCountLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        return calculateTimeGradient(solverScope.getScoreDirector());
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        return calculateTimeGradient(phaseScope.getScoreDirector());
    }

    protected double calculateTimeGradient(InnerScoreDirector scoreDirector) {
        long scoreCalculationCount = scoreDirector.getCalculationCount();
        double timeGradient = ((double) scoreCalculationCount) / ((double) scoreCalculationCountLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public ScoreCalculationCountTermination createChildThreadTermination(
            DefaultSolverScope solverScope, ChildThreadType childThreadType) {
        if (childThreadType == ChildThreadType.PART_THREAD) {
            // The ScoreDirector.calculationCount of partitions is maxed, not summed.
            return new ScoreCalculationCountTermination(scoreCalculationCountLimit);
        } else {
            throw new IllegalStateException("The childThreadType (" + childThreadType + ") is not implemented.");
        }
    }

    @Override
    public String toString() {
        return "ScoreCalculationCount(" + scoreCalculationCountLimit + ")";
    }

}
