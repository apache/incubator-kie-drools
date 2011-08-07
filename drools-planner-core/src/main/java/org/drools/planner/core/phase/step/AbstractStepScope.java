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

package org.drools.planner.core.phase.step;

import java.util.Random;

import org.drools.WorkingMemory;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;

public abstract class AbstractStepScope {

    protected int stepIndex = -1;

    protected boolean solutionInitialized = false;
    protected Score score = null;
    protected Boolean bestScoreImproved;
    // Stays null if there is no need to clone it
    protected Solution clonedSolution = null;

    public abstract AbstractSolverPhaseScope getSolverPhaseScope();

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public boolean isSolutionInitialized() {
        return solutionInitialized;
    }

    public void setSolutionInitialized(boolean solutionInitialized) {
        this.solutionInitialized = solutionInitialized;
    }

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

    public Solution getWorkingSolution() {
        return getSolverPhaseScope().getWorkingSolution();
    }

    public WorkingMemory getWorkingMemory() {
        return getSolverPhaseScope().getWorkingMemory();
    }

    public Random getWorkingRandom() {
        return getSolverPhaseScope().getWorkingRandom();
    }

    public Solution createOrGetClonedSolution() {
        if (clonedSolution == null) {
            clonedSolution = getWorkingSolution().cloneSolution();
        }
        return clonedSolution;
    }

}
