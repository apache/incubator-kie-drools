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

package org.optaplanner.core.impl.score.director.drools;

import java.util.Collection;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Drools implementation of {@link ScoreDirector}, which directs the Rule Engine to calculate the {@link Score}
 * of the {@link Solution} workingSolution.
 * @see ScoreDirector
 */
public class DroolsScoreDirector extends AbstractScoreDirector<DroolsScoreDirectorFactory> {

    public static final String GLOBAL_SCORE_HOLDER_KEY = "scoreHolder";

    protected KieSession kieSession;
    protected ScoreHolder workingScoreHolder;

    public DroolsScoreDirector(DroolsScoreDirectorFactory scoreDirectorFactory,
            boolean constraintMatchEnabledPreference) {
        super(scoreDirectorFactory, constraintMatchEnabledPreference);
    }

    public KieSession getKieSession() {
        return kieSession;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public void setWorkingSolution(Solution workingSolution) {
        super.setWorkingSolution(workingSolution);
        resetKieSession();
    }

    private void resetKieSession() {
        if (kieSession != null) {
            kieSession.dispose();
        }
        kieSession = scoreDirectorFactory.newKieSession();
        workingScoreHolder = getScoreDefinition().buildScoreHolder(constraintMatchEnabledPreference);
        kieSession.setGlobal(GLOBAL_SCORE_HOLDER_KEY, workingScoreHolder);
        // TODO Adjust when uninitialized entities from getWorkingFacts get added automatically too (and call afterEntityAdded)
        Collection<Object> workingFacts = getWorkingFacts();
        for (Object fact : workingFacts) {
            kieSession.insert(fact);
        }
    }

    public Collection<Object> getWorkingFacts() {
        return getSolutionDescriptor().getAllFacts(workingSolution);
    }

    public Score calculateScore() {
        variableListenerSupport.assertNotificationQueuesAreEmpty();
        kieSession.fireAllRules();
        Score score = workingScoreHolder.extractScore();
        setCalculatedScore(score);
        return score;
    }

    public boolean isConstraintMatchEnabled() {
        return workingScoreHolder.isConstraintMatchEnabled();
    }

    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        if (workingSolution == null) {
            throw new IllegalStateException(
                    "The method setWorkingSolution() must be called before the method getConstraintMatchTotals().");
        }
        kieSession.fireAllRules();
        return workingScoreHolder.getConstraintMatchTotals();
    }

    @Override
    public DroolsScoreDirector clone() {
        // TODO experiment with serializing the KieSession to clone it and its entities but not its other facts.
        // See drools-compiler's test SerializationHelper.getSerialisedStatefulKnowledgeSession(...)
        // and use an identity FactFactory that:
        // - returns the reference for a non-@PlanningEntity fact
        // - returns a clone for a @PlanningEntity fact (Pitfall: chained planning entities)
        // Note: currently that will break incremental score calculation, but future drools versions might fix that
        return (DroolsScoreDirector) super.clone();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (kieSession != null) {
            kieSession.dispose();
            kieSession = null;
        }
    }

    // ************************************************************************
    // Entity/variable add/change/remove methods
    // ************************************************************************

    // public void beforeEntityAdded(EntityDescriptor entityDescriptor, Object entity) // Do nothing

    @Override
    public void afterEntityAdded(EntityDescriptor entityDescriptor, Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The entity (" + entity + ") cannot be added to the ScoreDirector.");
        }
        if (!getSolutionDescriptor().hasEntityDescriptor(entity.getClass())) {
            throw new IllegalArgumentException("The entity (" + entity + ") of class (" + entity.getClass()
                    + ") is not a configured @PlanningEntity.");
        }
        if (kieSession.getFactHandle(entity) != null) {
            throw new IllegalArgumentException("The entity (" + entity
                    + ") was already added to this ScoreDirector."
                    + " Usually the cause is that that specific instance was already in your Solution's entities" +
                    " and you probably want to use before/afterVariableChanged() instead.");
        }
        kieSession.insert(entity);
        super.afterEntityAdded(entityDescriptor, entity);
    }

    // public void beforeVariableChanged(VariableDescriptor variableDescriptor, Object entity) // Do nothing

    @Override
    public void afterVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        update(entity);
        super.afterVariableChanged(variableDescriptor, entity);
    }

    private void update(Object entity) {
        FactHandle factHandle = kieSession.getFactHandle(entity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The entity (" + entity
                    + ") was never added to this ScoreDirector."
                    + " Usually the cause is that that specific instance was not in your Solution's entities.");
        }
        kieSession.update(factHandle, entity);
    }

    // public void beforeEntityRemoved(EntityDescriptor entityDescriptor, Object entity) // Do nothing

    @Override
    public void afterEntityRemoved(EntityDescriptor entityDescriptor, Object entity) {
        FactHandle factHandle = kieSession.getFactHandle(entity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The entity (" + entity
                    + ") was never added to this ScoreDirector."
                    + " Usually the cause is that that specific instance was not in your Solution's entities.");
        }
        kieSession.delete(factHandle);
        super.afterEntityRemoved(entityDescriptor, entity);
    }


    // ************************************************************************
    // Problem fact add/change/remove methods
    // ************************************************************************

    // public void beforeProblemFactAdded(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactAdded(Object problemFact) {
        if (kieSession.getFactHandle(problemFact) != null) {
            throw new IllegalArgumentException("The problemFact (" + problemFact
                    + ") was already added to this ScoreDirector."
                    + " Usually the cause is that that specific instance was already in your Solution's getProblemFacts()"
                    + " and you probably want to use before/afterProblemFactChanged() instead.");
        }
        kieSession.insert(problemFact);
        super.afterProblemFactAdded(problemFact);
    }

    // public void beforeProblemFactChanged(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactChanged(Object problemFact) {
        FactHandle factHandle = kieSession.getFactHandle(problemFact);
        if (factHandle == null) {
            throw new IllegalArgumentException("The problemFact (" + problemFact
                    + ") was never added to this ScoreDirector."
                    + " Usually the cause is that that specific instance was not in your Solution's getProblemFacts().");
        }
        kieSession.update(factHandle, problemFact);
        super.afterProblemFactChanged(problemFact);
    }

    // public void beforeProblemFactRemoved(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        FactHandle factHandle = kieSession.getFactHandle(problemFact);
        if (factHandle == null) {
            throw new IllegalArgumentException("The problemFact (" + problemFact
                    + ") was never added to this ScoreDirector."
                    + " Usually the cause is that that specific instance was not in your Solution's getProblemFacts().");
        }
        kieSession.delete(factHandle);
        super.afterProblemFactRemoved(problemFact);
    }

}
