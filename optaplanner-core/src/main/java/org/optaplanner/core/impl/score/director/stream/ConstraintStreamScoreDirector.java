/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.director.stream;

import java.util.Collection;
import java.util.Map;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.stream.ConstraintSession;

/**
 * FP streams implementation of {@link ScoreDirector}, which only recalculates the {@link Score}
 * of the part of the {@link PlanningSolution working solution} that changed,
 * instead of the going through the entire {@link PlanningSolution}. This is incremental calculation, which is fast.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see ScoreDirector
 */
public class ConstraintStreamScoreDirector<Solution_>
        extends AbstractScoreDirector<Solution_, ConstraintStreamScoreDirectorFactory<Solution_>> {

    protected ConstraintSession<Solution_> session;

    public ConstraintStreamScoreDirector(ConstraintStreamScoreDirectorFactory<Solution_> scoreDirectorFactory,
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference) {
        super(scoreDirectorFactory, lookUpEnabled, constraintMatchEnabledPreference);
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public void setWorkingSolution(Solution_ workingSolution) {
        super.setWorkingSolution(workingSolution);
        resetConstraintStreamingSession();
    }

    private void resetConstraintStreamingSession() {
        session = scoreDirectorFactory.newConstraintStreamingSession(constraintMatchEnabledPreference, workingSolution);
        Collection<Object> workingFacts = getSolutionDescriptor().getAllFacts(workingSolution);
        for (Object fact : workingFacts) {
            session.insert(fact);
        }
    }

    @Override
    public Score calculateScore() {
        variableListenerSupport.assertNotificationQueuesAreEmpty();
        Score score = session.calculateScore(workingInitScore);
        setCalculatedScore(score);
        return score;
    }

    @Override
    public boolean isConstraintMatchEnabled() {
        return constraintMatchEnabledPreference;
    }

    @Override
    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        if (workingSolution == null) {
            throw new IllegalStateException(
                    "The method setWorkingSolution() must be called before the method getConstraintMatchTotals().");
        }
        return session.getConstraintMatchTotalMap().values();
    }

    @Override
    public Map<String, ConstraintMatchTotal> getConstraintMatchTotalMap() {
        if (workingSolution == null) {
            throw new IllegalStateException(
                    "The method setWorkingSolution() must be called before the method getConstraintMatchTotalMap().");
        }
        return session.getConstraintMatchTotalMap();
    }

    @Override
    public Map<Object, Indictment> getIndictmentMap() {
        if (workingSolution == null) {
            throw new IllegalStateException(
                    "The method setWorkingSolution() must be called before the method getIndictmentMap().");
        }
        return session.getIndictmentMap();
    }

    @Override
    public void close() {
        super.close();
        session.close();
        session = null;
    }

    // ************************************************************************
    // Entity/variable add/change/remove methods
    // ************************************************************************

    // public void beforeEntityAdded(EntityDescriptor entityDescriptor, Object entity) // Do nothing

    @Override
    public void afterEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The entity (" + entity + ") cannot be added to the ScoreDirector.");
        }
        if (!getSolutionDescriptor().hasEntityDescriptor(entity.getClass())) {
            throw new IllegalArgumentException("The entity (" + entity + ") of class (" + entity.getClass()
                    + ") is not a configured @PlanningEntity.");
        }
        session.insert(entity);
        super.afterEntityAdded(entityDescriptor, entity);
    }

    // public void beforeVariableChanged(VariableDescriptor variableDescriptor, Object entity) // Do nothing

    @Override
    public void afterVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        session.update(entity);
        super.afterVariableChanged(variableDescriptor, entity);
    }

    // public void beforeEntityRemoved(EntityDescriptor entityDescriptor, Object entity) // Do nothing

    @Override
    public void afterEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        session.retract(entity);
        super.afterEntityRemoved(entityDescriptor, entity);
    }

    // ************************************************************************
    // Problem fact add/change/remove methods
    // ************************************************************************

    // public void beforeProblemFactAdded(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactAdded(Object problemFact) {
        if (problemFact == null) {
            throw new IllegalArgumentException("The problemFact (" + problemFact + ") cannot be added to the ScoreDirector.");
        }
        session.insert(problemFact);
        super.afterProblemFactAdded(problemFact);
    }

    // public void beforeProblemPropertyChanged(Object problemFactOrEntity) // Do nothing

    @Override
    public void afterProblemPropertyChanged(Object problemFactOrEntity) {
        session.update(problemFactOrEntity);
        super.afterProblemPropertyChanged(problemFactOrEntity);
    }

    // public void beforeProblemFactRemoved(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        session.retract(problemFact);
        super.afterProblemFactRemoved(problemFact);
    }

}
