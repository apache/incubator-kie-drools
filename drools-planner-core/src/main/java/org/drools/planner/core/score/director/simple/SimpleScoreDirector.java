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

package org.drools.planner.core.score.director.simple;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.director.AbstractScoreDirector;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;

/**
 * Simple java implementation of {@link ScoreDirector}, which recalculates the {@link Score}
 * of the {@link Solution} workingSolution every time. This is non-incremental calculation, which is slow.
 * @see ScoreDirector
 */
public class SimpleScoreDirector extends AbstractScoreDirector<SimpleScoreDirectorFactory> {

    private final SimpleScoreCalculator simpleScoreCalculator;

    public SimpleScoreDirector(SimpleScoreDirectorFactory scoreDirectorFactory,
            SimpleScoreCalculator simpleScoreCalculator) {
        super(scoreDirectorFactory);
        this.simpleScoreCalculator = simpleScoreCalculator;
    }

    public void setWorkingSolution(Solution workingSolution) {
        this.workingSolution = workingSolution;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        // Do nothing
    }

    public void beforeAllVariablesChanged(Object entity) {
        // Do nothing
    }

    public void afterAllVariablesChanged(Object entity) {
        // Do nothing
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        // Do nothing
    }

    public void afterVariableChanged(Object entity, String variableName) {
        // Do nothing
    }

    public void beforeEntityRemoved(Object entity) {
        // Do nothing
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    public void beforeProblemFactAdded(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactAdded(Object problemFact) {
        // Do nothing
    }

    public void beforeProblemFactChanged(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactChanged(Object problemFact) {
        // Do nothing
    }

    public void beforeProblemFactRemoved(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactRemoved(Object problemFact) {
        // Do nothing
    }

    public Score calculateScore() {
        Score score = simpleScoreCalculator.calculateScore(workingSolution);
        workingSolution.setScore(score);
        calculateCount++;
        return score;
    }

    public void dispose() {
        // Do nothing
    }

}
