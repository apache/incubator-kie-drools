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
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
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

    public Score calculateScore() {
        Score score = incrementalScoreCalculator.calculateScore();
        setCalculatedScore(score);
        return score;
    }

    // ************************************************************************
    // Entity/variable add/change/remove methods
    // ************************************************************************

    @Override
    public void beforeEntityAdded(EntityDescriptor entityDescriptor, Object entity) {
        incrementalScoreCalculator.beforeEntityAdded(entity);
        super.beforeEntityAdded(entityDescriptor, entity);
    }

    @Override
    public void afterEntityAdded(EntityDescriptor entityDescriptor, Object entity) {
        incrementalScoreCalculator.afterEntityAdded(entity);
        super.afterEntityAdded(entityDescriptor, entity);
    }

    @Override
    public void beforeVariableChanged(GenuineVariableDescriptor variableDescriptor, Object entity) {
        incrementalScoreCalculator.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        super.beforeVariableChanged(variableDescriptor, entity);
    }

    @Override
    public void afterVariableChanged(GenuineVariableDescriptor variableDescriptor, Object entity) {
        incrementalScoreCalculator.afterVariableChanged(entity, variableDescriptor.getVariableName());
        super.afterVariableChanged(variableDescriptor, entity);
    }

    @Override
    public void beforeShadowVariableChanged(Object entity, String variableName) {
        incrementalScoreCalculator.beforeVariableChanged(entity, variableName);
        super.beforeShadowVariableChanged(entity, variableName);
    }

    @Override
    public void afterShadowVariableChanged(Object entity, String variableName) {
        incrementalScoreCalculator.afterVariableChanged(entity, variableName);
        super.afterShadowVariableChanged(entity, variableName);
    }

    @Override
    public void beforeEntityRemoved(EntityDescriptor entityDescriptor, Object entity) {
        incrementalScoreCalculator.beforeEntityRemoved(entity);
        super.beforeEntityRemoved(entityDescriptor, entity);
    }

    @Override
    public void afterEntityRemoved(EntityDescriptor entityDescriptor, Object entity) {
        incrementalScoreCalculator.afterEntityRemoved(entity);
        super.afterEntityRemoved(entityDescriptor, entity);
    }

    // ************************************************************************
    // Problem fact add/change/remove methods
    // ************************************************************************

    @Override
    public void beforeProblemFactAdded(Object problemFact) {
        super.beforeProblemFactAdded(problemFact);
    }

    @Override
    public void afterProblemFactAdded(Object problemFact) {
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
        super.afterProblemFactAdded(problemFact);
    }

    @Override
    public void beforeProblemFactChanged(Object problemFact) {
        super.beforeProblemFactChanged(problemFact);
    }

    @Override
    public void afterProblemFactChanged(Object problemFact) {
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
        super.afterProblemFactChanged(problemFact);
    }

    @Override
    public void beforeProblemFactRemoved(Object problemFact) {
        super.beforeProblemFactRemoved(problemFact);
    }

    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
        super.afterProblemFactRemoved(problemFact);
    }

    // ************************************************************************
    // Assert methods
    // ************************************************************************

    @Override
    protected String buildScoreCorruptionAnalysis(ScoreDirector uncorruptedScoreDirector) {
        if (!(uncorruptedScoreDirector instanceof IncrementalScoreDirector)) {
            return "  Score corruption analysis could not be generated because "
                    + "the uncorruptedScoreDirector class (" + uncorruptedScoreDirector.getClass()
                    + ") is not an instance of the scoreDirector class (" + IncrementalScoreDirector.class + ").\n"
                    + "  Check your score constraints manually.";
        }
        IncrementalScoreDirector uncorruptedIncrementalScoreDirector
                = (IncrementalScoreDirector) uncorruptedScoreDirector;
        return incrementalScoreCalculator.buildScoreCorruptionAnalysis(
                uncorruptedIncrementalScoreDirector.incrementalScoreCalculator);
    }

}
