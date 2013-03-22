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

package org.optaplanner.core.impl.phase.step;

import java.util.Random;

import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;

public abstract class AbstractStepScope {

    protected int stepIndex = -1;

    protected Score score = null;
    protected Boolean bestScoreImproved;
    // Stays null if there is no need to clone it
    protected Solution clonedSolution = null;

    public abstract AbstractSolverPhaseScope getPhaseScope();

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public abstract boolean isBestSolutionCloningDelayed();

    public abstract int getUninitializedVariableCount();

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Boolean getBestScoreImproved() {
        return bestScoreImproved;
    }

    public void setBestScoreImproved(Boolean bestScoreImproved) {
        this.bestScoreImproved = bestScoreImproved;
    }

    public Solution getClonedSolution() {
        return clonedSolution;
    }

    public void setClonedSolution(Solution clonedSolution) {
        this.clonedSolution = clonedSolution;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public ScoreDirector getScoreDirector() {
        return getPhaseScope().getScoreDirector();
    }

    public Solution getWorkingSolution() {
        return getPhaseScope().getWorkingSolution();
    }

    public Random getWorkingRandom() {
        return getPhaseScope().getWorkingRandom();
    }

    public Solution createOrGetClonedSolution() {
        if (clonedSolution == null) {
            clonedSolution = getScoreDirector().cloneWorkingSolution();
        }
        return clonedSolution;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + stepIndex + ")";
    }

}
