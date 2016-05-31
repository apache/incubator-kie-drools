/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.phase.scope;

import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractStepScope<Solution_> {

    protected final int stepIndex;

    protected Score score = null;
    protected Boolean bestScoreImproved;
    // Stays null if there is no need to clone it
    protected Solution_ clonedSolution = null;

    public abstract AbstractPhaseScope<Solution_> getPhaseScope();

    public AbstractStepScope(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public int getStepIndex() {
        return stepIndex;
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

    public Solution_ getClonedSolution() {
        return clonedSolution;
    }

    public void setClonedSolution(Solution_ clonedSolution) {
        this.clonedSolution = clonedSolution;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public InnerScoreDirector<Solution_> getScoreDirector() {
        return getPhaseScope().getScoreDirector();
    }

    public Solution_ getWorkingSolution() {
        return getPhaseScope().getWorkingSolution();
    }

    public Random getWorkingRandom() {
        return getPhaseScope().getWorkingRandom();
    }

    public Solution_ createOrGetClonedSolution() {
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
