/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Incremental java implementation of {@link ScoreDirector}, which only recalculates the {@link Score}
 * of the part of the {@link PlanningSolution working solution} that changed,
 * instead of the going through the entire {@link PlanningSolution}. This is incremental calculation, which is fast.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see ScoreDirector
 */
public class IncrementalScoreDirector<Solution_>
        extends AbstractScoreDirector<Solution_, IncrementalScoreDirectorFactory<Solution_>> {

    private final IncrementalScoreCalculator<Solution_> incrementalScoreCalculator;

    public IncrementalScoreDirector(IncrementalScoreDirectorFactory<Solution_> scoreDirectorFactory,
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference,
            IncrementalScoreCalculator<Solution_> incrementalScoreCalculator) {
        super(scoreDirectorFactory, lookUpEnabled, constraintMatchEnabledPreference);
        this.incrementalScoreCalculator = incrementalScoreCalculator;
    }

    public IncrementalScoreCalculator<Solution_> getIncrementalScoreCalculator() {
        return incrementalScoreCalculator;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public void setWorkingSolution(Solution_ workingSolution) {
        super.setWorkingSolution(workingSolution);
        if (incrementalScoreCalculator instanceof ConstraintMatchAwareIncrementalScoreCalculator) {
            ((ConstraintMatchAwareIncrementalScoreCalculator<Solution_>) incrementalScoreCalculator)
                    .resetWorkingSolution(workingSolution, constraintMatchEnabledPreference);
        } else {
            incrementalScoreCalculator.resetWorkingSolution(workingSolution);
        }
    }

    @Override
    public Score calculateScore() {
        variableListenerSupport.assertNotificationQueuesAreEmpty();
        Score score = incrementalScoreCalculator.calculateScore();
        if (score == null) {
            throw new IllegalStateException("The incrementalScoreCalculator (" + incrementalScoreCalculator.getClass()
                    + ") must return a non-null score (" + score + ") in the method calculateScore().");
        }
        if (workingInitScore != 0) {
            score = score.withInitScore(workingInitScore);
        }
        setCalculatedScore(score);
        return score;
    }

    @Override
    public boolean isConstraintMatchEnabled() {
        return constraintMatchEnabledPreference
                && incrementalScoreCalculator instanceof ConstraintMatchAwareIncrementalScoreCalculator;
    }

    @Override
    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        if (!isConstraintMatchEnabled()) {
            throw new IllegalStateException("When constraintMatchEnabled (" + isConstraintMatchEnabled()
                    + ") is disabled in the constructor, this method should not be called.");
        }
        // Notice that we don't trigger the variable listeners
        return ((ConstraintMatchAwareIncrementalScoreCalculator<Solution_>) incrementalScoreCalculator)
                .getConstraintMatchTotals();
    }

    @Override
    public Map<String, ConstraintMatchTotal> getConstraintMatchTotalMap() {
        return getConstraintMatchTotals().stream()
                .collect(Collectors.toMap(ConstraintMatchTotal::getConstraintId, Function.identity()));
    }

    @Override
    public Map<Object, Indictment> getIndictmentMap() {
        if (!isConstraintMatchEnabled()) {
            throw new IllegalStateException("When constraintMatchEnabled (" + isConstraintMatchEnabled()
                    + ") is disabled in the constructor, this method should not be called.");
        }
        Map<Object, Indictment> incrementalIndictmentMap
                = ((ConstraintMatchAwareIncrementalScoreCalculator<Solution_>) incrementalScoreCalculator)
                .getIndictmentMap();
        if (incrementalIndictmentMap != null) {
            return incrementalIndictmentMap;
        }
        Map<Object, Indictment> indictmentMap = new LinkedHashMap<>(); // TODO use entitySize
        Score zeroScore = getScoreDefinition().getZeroScore();
        for (ConstraintMatchTotal constraintMatchTotal : getConstraintMatchTotals()) {
            for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
                constraintMatch.getJustificationList().stream()
                        .distinct() // One match might have the same justification twice
                        .forEach(justification -> {
                            Indictment indictment = indictmentMap.computeIfAbsent(justification,
                                    k -> new Indictment(justification, zeroScore));
                            indictment.addConstraintMatch(constraintMatch);
                        });
            }
        }
        return indictmentMap;
    }

    // ************************************************************************
    // Entity/variable add/change/remove methods
    // ************************************************************************

    @Override
    public void beforeEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        incrementalScoreCalculator.beforeEntityAdded(entity);
        super.beforeEntityAdded(entityDescriptor, entity);
    }

    @Override
    public void afterEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        incrementalScoreCalculator.afterEntityAdded(entity);
        super.afterEntityAdded(entityDescriptor, entity);
    }

    @Override
    public void beforeVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        incrementalScoreCalculator.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        super.beforeVariableChanged(variableDescriptor, entity);
    }

    @Override
    public void afterVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        incrementalScoreCalculator.afterVariableChanged(entity, variableDescriptor.getVariableName());
        super.afterVariableChanged(variableDescriptor, entity);
    }

    @Override
    public void beforeEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        incrementalScoreCalculator.beforeEntityRemoved(entity);
        super.beforeEntityRemoved(entityDescriptor, entity);
    }

    @Override
    public void afterEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
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
    public void beforeProblemPropertyChanged(Object problemFactOrEntity) {
        super.beforeProblemPropertyChanged(problemFactOrEntity);
    }

    @Override
    public void afterProblemPropertyChanged(Object problemFactOrEntity) {
        incrementalScoreCalculator.resetWorkingSolution(workingSolution); // TODO do not nuke it
        super.afterProblemPropertyChanged(problemFactOrEntity);
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

}
