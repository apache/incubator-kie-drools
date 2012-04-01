/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.score.director.incremental;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.director.AbstractScoreDirector;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;

/**
 * Incremental java implementation of {@link ScoreDirector}, which only recalculates the {@link Score}
 * of the part of the {@link Solution} workingSolution that changed,
 * instead of the going through the entire {@link Solution}. This is incremental calculation, which is fast.
 * @see ScoreDirector
 */
public class IncrementalScoreDirector extends AbstractScoreDirector<IncrementalScoreDirectorFactory> {

    private final IncrementalScoreCalculator incrementalScoreCalculator;

    public IncrementalScoreDirector(IncrementalScoreDirectorFactory scoreDirectorFactory,
            IncrementalScoreCalculator incrementalScoreCalculator) {
        super(scoreDirectorFactory);
        this.incrementalScoreCalculator = incrementalScoreCalculator;
    }

    public void setWorkingSolution(Solution workingSolution) {
        this.workingSolution = workingSolution;
        incrementalScoreCalculator.resetWorkingSolution(workingSolution);
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public void beforeEntityAdded(Object entity) {
        incrementalScoreCalculator.beforeEntityAdded(entity);
    }

    public void afterEntityAdded(Object entity) {
        incrementalScoreCalculator.afterEntityAdded(entity);
    }

    public void beforeAllVariablesChanged(Object entity) {
        incrementalScoreCalculator.beforeAllVariablesChanged(entity);
    }

    public void afterAllVariablesChanged(Object entity) {
        incrementalScoreCalculator.afterAllVariablesChanged(entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        incrementalScoreCalculator.beforeVariableChanged(entity, variableName);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        incrementalScoreCalculator.afterVariableChanged(entity, variableName);
    }

    public void beforeEntityRemoved(Object entity) {
        incrementalScoreCalculator.beforeEntityRemoved(entity);
    }

    public void afterEntityRemoved(Object entity) {
        incrementalScoreCalculator.afterEntityRemoved(entity);
    }

    public void beforeProblemFactAdded(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactAdded(Object problemFact) {
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
    }

    public void beforeProblemFactChanged(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactChanged(Object problemFact) {
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
    }

    public void beforeProblemFactRemoved(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactRemoved(Object problemFact) {
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
    }

    public Score calculateScore() {
        Score score = incrementalScoreCalculator.calculateScore(workingSolution);
        workingSolution.setScore(score);
        calculateCount++;
        return score;
    }

    public void dispose() {
        // Do nothing
    }

}
