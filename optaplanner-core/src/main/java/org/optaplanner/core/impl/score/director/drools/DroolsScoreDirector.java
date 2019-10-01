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
import java.util.Map;
import java.util.function.Function;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Drools implementation of {@link ScoreDirector}, which directs the Rule Engine to calculate the {@link Score}
 * of the {@link PlanningSolution working solution}.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see ScoreDirector
 */
public class DroolsScoreDirector<Solution_>
        extends AbstractScoreDirector<Solution_, DroolsScoreDirectorFactory<Solution_>> {

    public static final String GLOBAL_SCORE_HOLDER_KEY = "scoreHolder";

    protected KieSession kieSession;
    protected ScoreHolder scoreHolder;

    public DroolsScoreDirector(DroolsScoreDirectorFactory<Solution_> scoreDirectorFactory,
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference) {
        super(scoreDirectorFactory, lookUpEnabled, constraintMatchEnabledPreference);
    }

    public KieSession getKieSession() {
        return kieSession;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public void setWorkingSolution(Solution_ workingSolution) {
        super.setWorkingSolution(workingSolution);
        resetKieSession();
    }

    private void resetKieSession() {
        if (kieSession != null) {
            kieSession.dispose();
        }
        kieSession = scoreDirectorFactory.newKieSession();
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener());
        resetScoreHolder();
        // TODO Adjust when uninitialized entities from getWorkingFacts get added automatically too (and call afterEntityAdded)
        Collection<Object> workingFacts = getWorkingFacts();
        for (Object fact : workingFacts) {
            kieSession.insert(fact);
        }
    }

    private void resetScoreHolder() {
        scoreHolder = getScoreDefinition().buildScoreHolder(constraintMatchEnabledPreference);
        scoreDirectorFactory.getRuleToConstraintWeightExtractorMap().forEach(
                (Rule rule, Function<Solution_, Score<?>> extractor) -> {
            Score<?> constraintWeight = extractor.apply(workingSolution);
            getSolutionDescriptor().validateConstraintWeight(rule.getPackageName(), rule.getName(), constraintWeight);
            scoreHolder.configureConstraintWeight(rule, constraintWeight);
        });
        kieSession.setGlobal(GLOBAL_SCORE_HOLDER_KEY, scoreHolder);
    }

    public Collection<Object> getWorkingFacts() {
        return getSolutionDescriptor().getAllFacts(workingSolution);
    }

    @Override
    public Score calculateScore() {
        variableListenerSupport.assertNotificationQueuesAreEmpty();
        kieSession.fireAllRules();
        Score score = scoreHolder.extractScore(workingInitScore);
        setCalculatedScore(score);
        return score;
    }

    @Override
    public boolean isConstraintMatchEnabled() {
        return scoreHolder.isConstraintMatchEnabled();
    }

    @Override
    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        if (workingSolution == null) {
            throw new IllegalStateException(
                    "The method setWorkingSolution() must be called before the method getConstraintMatchTotals().");
        }
        // Notice that we don't trigger the variable listeners
        kieSession.fireAllRules();
        return scoreHolder.getConstraintMatchTotals();
    }

    @Override
    public Map<String, ConstraintMatchTotal> getConstraintMatchTotalMap() {
        if (workingSolution == null) {
            throw new IllegalStateException(
                    "The method setWorkingSolution() must be called before the method getConstraintMatchTotalMap().");
        }
        // Notice that we don't trigger the variable listeners
        kieSession.fireAllRules();
        return scoreHolder.getConstraintMatchTotalMap();
    }

    @Override
    public Map<Object, Indictment> getIndictmentMap() {
        if (workingSolution == null) {
            throw new IllegalStateException(
                    "The method setWorkingSolution() must be called before the method getIndictmentMap().");
        }
        // Notice that we don't trigger the variable listeners
        kieSession.fireAllRules();
        return scoreHolder.getIndictmentMap();
    }

    @Override
    public void close() {
        super.close();
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
    public void afterEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
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
        update(entity, variableDescriptor.getVariableName());
        super.afterVariableChanged(variableDescriptor, entity);
    }

    private void update(Object entity, String variableName) {
        FactHandle factHandle = kieSession.getFactHandle(entity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The entity (" + entity
                    + ") was never added to this ScoreDirector.\n"
                    + "Maybe that specific instance is not in the return values of the "
                    + PlanningSolution.class.getSimpleName() + "'s entity members ("
                    + getSolutionDescriptor().getEntityMemberAndEntityCollectionMemberNames() + ").");
        }
        kieSession.update(factHandle, entity, variableName);
    }

    // public void beforeEntityRemoved(EntityDescriptor entityDescriptor, Object entity) // Do nothing

    @Override
    public void afterEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        FactHandle factHandle = kieSession.getFactHandle(entity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The entity (" + entity
                    + ") was never added to this ScoreDirector.\n"
                    + "Maybe that specific instance is not in the return values of the "
                    + PlanningSolution.class.getSimpleName() + "'s entity members ("
                    + getSolutionDescriptor().getEntityMemberAndEntityCollectionMemberNames() + ").");
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
                    + ") was already added to this ScoreDirector.\n"
                    + "Maybe that specific instance is already in the "
                    + PlanningSolution.class.getSimpleName() + "'s problem fact members ("
                    + getSolutionDescriptor().getProblemFactMemberAndProblemFactCollectionMemberNames() + ").\n"
                    + "Maybe use before/afterProblemPropertyChanged() instead of before/afterProblemFactAdded().");
        }
        kieSession.insert(problemFact);
        super.afterProblemFactAdded(problemFact);
    }

    // public void beforeProblemPropertyChanged(Object problemFactOrEntity) // Do nothing

    @Override
    public void afterProblemPropertyChanged(Object problemFactOrEntity) {
        FactHandle factHandle = kieSession.getFactHandle(problemFactOrEntity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The problemFact (" + problemFactOrEntity
                    + ") was never added to this ScoreDirector.\n"
                    + "Maybe that specific instance is not in the "
                    + PlanningSolution.class.getSimpleName() + "'s problem fact members ("
                    + getSolutionDescriptor().getProblemFactMemberAndProblemFactCollectionMemberNames() + ").\n"
                    + "Maybe first translate that external instance to the workingSolution's instance"
                    + " with " + ScoreDirector.class.getSimpleName() + ".lookUpWorkingObject().");
        }
        kieSession.update(factHandle, problemFactOrEntity);
        super.afterProblemPropertyChanged(problemFactOrEntity);
    }

    // public void beforeProblemFactRemoved(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        FactHandle factHandle = kieSession.getFactHandle(problemFact);
        if (factHandle == null) {
            throw new IllegalArgumentException("The problemFact (" + problemFact
                    + ") was never added to this ScoreDirector.\n"
                    + "Maybe that specific instance is not in the "
                    + PlanningSolution.class.getSimpleName() + "'s problem fact members ("
                    + getSolutionDescriptor().getProblemFactMemberAndProblemFactCollectionMemberNames() + ").\n"
                    + "Maybe first translate that external instance to the workingSolution's instance"
                    + " with " + ScoreDirector.class.getSimpleName() + ".lookUpWorkingObject().");
        }
        kieSession.delete(factHandle);
        super.afterProblemFactRemoved(problemFact);
    }

}
