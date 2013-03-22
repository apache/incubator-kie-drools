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

package org.optaplanner.core.impl.score.director.incremental;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;

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

    public IncrementalScoreCalculator getIncrementalScoreCalculator() {
        return incrementalScoreCalculator;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public void setWorkingSolution(Solution workingSolution) {
        super.setWorkingSolution(workingSolution);
        incrementalScoreCalculator.resetWorkingSolution(workingSolution);
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        incrementalScoreCalculator.beforeEntityAdded(entity);
        super.beforeEntityAdded(entity);
    }

    @Override
    public void afterEntityAdded(Object entity) {
        super.afterEntityAdded(entity);
        incrementalScoreCalculator.afterEntityAdded(entity);
    }

    @Override
    public void beforeAllVariablesChanged(Object entity) {
        incrementalScoreCalculator.beforeAllVariablesChanged(entity);
        super.beforeAllVariablesChanged(entity);
    }

    @Override
    public void afterAllVariablesChanged(Object entity) {
        super.afterAllVariablesChanged(entity);
        incrementalScoreCalculator.afterAllVariablesChanged(entity);
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        incrementalScoreCalculator.beforeVariableChanged(entity, variableName);
        super.beforeVariableChanged(entity, variableName);
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        super.afterVariableChanged(entity, variableName);
        incrementalScoreCalculator.afterVariableChanged(entity, variableName);
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        incrementalScoreCalculator.beforeEntityRemoved(entity);
        super.beforeEntityRemoved(entity);
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        super.afterEntityRemoved(entity);
        incrementalScoreCalculator.afterEntityRemoved(entity);
    }

    @Override
    public void beforeProblemFactAdded(Object problemFact) {
        super.beforeProblemFactAdded(problemFact);
    }

    @Override
    public void afterProblemFactAdded(Object problemFact) {
        super.afterProblemFactAdded(problemFact);
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
    }

    @Override
    public void beforeProblemFactChanged(Object problemFact) {
        super.beforeProblemFactChanged(problemFact);
    }

    @Override
    public void afterProblemFactChanged(Object problemFact) {
        super.afterProblemFactChanged(problemFact);
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
    }

    @Override
    public void beforeProblemFactRemoved(Object problemFact) {
        super.beforeProblemFactRemoved(problemFact);
    }

    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        super.afterProblemFactRemoved(problemFact);
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
    }

    public Score calculateScore() {
        Score score = incrementalScoreCalculator.calculateScore();
        setCalculatedScore(score);
        return score;
    }

    @Override
    protected String buildScoreCorruptionAnalysis(ScoreDirector uncorruptedScoreDirector) {
        if (!(uncorruptedScoreDirector instanceof IncrementalScoreDirector)) {
            return "Unable to analyze: the uncorruptedScoreDirector class (" + uncorruptedScoreDirector.getClass()
                    + ") is not an instance of the scoreDirector class (" + IncrementalScoreDirector.class + ").";
        }
        IncrementalScoreDirector uncorruptedIncrementalScoreDirector
                = (IncrementalScoreDirector) uncorruptedScoreDirector;
        return incrementalScoreCalculator.buildScoreCorruptionAnalysis(
                uncorruptedIncrementalScoreDirector.incrementalScoreCalculator);
    }

}
