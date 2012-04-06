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

package org.drools.planner.core.score.director;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link ScoreDirector}.
 * <p/>
 * Implementation note: Extending classes should follow these guidelines:
 * <ul>
 *     <li>before* method: last statement should be a call to the super method</li>
 *     <li>after* method: first statement should be a call to the super method</li>
 * </ul>
 * @see ScoreDirector
 */
public abstract class AbstractScoreDirector<F extends ScoreDirectorFactory> implements ScoreDirector {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final F scoreDirectorFactory;

    protected Solution workingSolution;
    protected boolean hasChainedVariables;
    protected Map<PlanningVariableDescriptor, Map<Object, Object>> chainedVariableToTrailingEntityMap;

    protected long calculateCount = 0L;

    protected AbstractScoreDirector(F scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        Collection<PlanningVariableDescriptor> chainedVariableDescriptors = getSolutionDescriptor()
                .getChainedVariableDescriptors();
        hasChainedVariables = !chainedVariableDescriptors.isEmpty();
        chainedVariableToTrailingEntityMap = new HashMap<PlanningVariableDescriptor, Map<Object, Object>>(
                chainedVariableDescriptors.size());
        for (PlanningVariableDescriptor chainedVariableDescriptor : chainedVariableDescriptors) {
            chainedVariableToTrailingEntityMap.put(chainedVariableDescriptor, null);
        }
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return scoreDirectorFactory.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDirectorFactory.getScoreDefinition();
    }

    public Solution getWorkingSolution() {
        return workingSolution;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public void setWorkingSolution(Solution workingSolution) {
        this.workingSolution = workingSolution;
        resetTrailingEntityMap();
    }

    private void resetTrailingEntityMap() {
        if (hasChainedVariables) {
            List<Object> entityList = getSolutionDescriptor().getPlanningEntityList(workingSolution);
            for (Map.Entry<PlanningVariableDescriptor, Map<Object, Object>> entry
                    : chainedVariableToTrailingEntityMap.entrySet()) {
                entry.setValue(new HashMap<Object, Object>(entityList.size()));
            }
            for (Object entity : entityList){
                insertInTrailingEntityMap(entity);
            }
        }
    }

    private void insertInTrailingEntityMap(Object entity) {
        if (hasChainedVariables) {
            for (Map.Entry<PlanningVariableDescriptor, Map<Object, Object>> entry
                    : chainedVariableToTrailingEntityMap.entrySet()) {
                PlanningVariableDescriptor variableDescriptor = entry.getKey();
                if (variableDescriptor.getPlanningEntityDescriptor().appliesToPlanningEntity(entity.getClass())) {
                    Object value = variableDescriptor.getValue(entity);
                    Map<Object, Object> valueToTrailingEntityMap = entry.getValue();
                    Object previousValue = valueToTrailingEntityMap.put(value, entity);
                    if (previousValue != null) {
                        throw new IllegalArgumentException("The chainedPlanningVariable ("
                                + variableDescriptor.getVariableName()
                                + ") for the entity (" + entity
                                + ") is trailing multiple values (" + value + ") (" + previousValue + ")."
                                + " This is a theoretically impossible and a bug in Planner.");
                    }
                }
            }
        }
    }

    private void retractFromTrailingEntityMap(Object entity) {
        if (hasChainedVariables) {
            for (Map.Entry<PlanningVariableDescriptor, Map<Object, Object>> entry
                    : chainedVariableToTrailingEntityMap.entrySet()) {
                PlanningVariableDescriptor variableDescriptor = entry.getKey();
                if (variableDescriptor.getPlanningEntityDescriptor().appliesToPlanningEntity(entity.getClass())) {
                    Object value = variableDescriptor.getValue(entity);
                    Map<Object, Object> valueToTrailingEntityMap = entry.getValue();
                    valueToTrailingEntityMap.put(value, null);
                }
            }
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insertInTrailingEntityMap(entity);
    }

    public void beforeAllVariablesChanged(Object entity) {
        retractFromTrailingEntityMap(entity);
    }

    public void afterAllVariablesChanged(Object entity) {
        insertInTrailingEntityMap(entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retractFromTrailingEntityMap(entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insertInTrailingEntityMap(entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retractFromTrailingEntityMap(entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    public void beforeProblemFactAdded(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactAdded(Object problemFact) {
        resetTrailingEntityMap(); // TODO do not nuke it
    }

    public void beforeProblemFactChanged(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactChanged(Object problemFact) {
        resetTrailingEntityMap(); // TODO do not nuke it
    }

    public void beforeProblemFactRemoved(Object problemFact) {
        // Do nothing
    }

    public void afterProblemFactRemoved(Object problemFact) {
        resetTrailingEntityMap(); // TODO do not nuke it
    }

    public List<Object> getWorkingPlanningEntityList() {
        return getSolutionDescriptor().getPlanningEntityList(workingSolution);
    }

    public boolean isWorkingSolutionInitialized() {
        return getSolutionDescriptor().isInitialized(workingSolution);
    }

    protected void setCalculatedScore(Score score) {
        workingSolution.setScore(score);
        calculateCount++;
    }

    public Map<Object, Object> getChainedValueToTrailingEntityMap(
            PlanningVariableDescriptor chainedVariableDescriptor) {
        return chainedVariableToTrailingEntityMap.get(chainedVariableDescriptor);
    }

    public Map<Object, List<Object>> getVariableToEntitiesMap(PlanningVariableDescriptor variableDescriptor) {
        List<Object> entityList = variableDescriptor.getPlanningEntityDescriptor().extractEntities(
                workingSolution);
        Map<Object, List<Object>> variableToEntitiesMap = new HashMap<Object, List<Object>>(entityList.size());
        for (Object entity : entityList) {
            Object variable = variableDescriptor.getValue(entity);
            List<Object> subEntities = variableToEntitiesMap.get(variable);
            if (subEntities == null) {
                subEntities = new ArrayList<Object>();
                variableToEntitiesMap.put(variable, subEntities);
            }
            subEntities.add(entity);
        }
        return variableToEntitiesMap;
    }

    public void assertWorkingScore(Score workingScore) {
        ScoreDirector uncorruptedScoreDirector = scoreDirectorFactory.buildScoreDirector();
        uncorruptedScoreDirector.setWorkingSolution(workingSolution);
        Score uncorruptedScore = uncorruptedScoreDirector.calculateScore();
        if (!workingScore.equals(uncorruptedScore)) {
            String scoreCorruptionAnalysis = buildScoreCorruptionAnalysis(uncorruptedScoreDirector);
            uncorruptedScoreDirector.dispose();
            throw new IllegalStateException(
                    "Score corruption: the workingScore (" + workingScore + ") is not the uncorruptedScore ("
                            + uncorruptedScore + ")"
                            + (scoreCorruptionAnalysis == null ? "." : ":\n" + scoreCorruptionAnalysis));
        } else {
            uncorruptedScoreDirector.dispose();
        }
    }

    protected String buildScoreCorruptionAnalysis(ScoreDirector uncorruptedScoreDirector) {
        // No analysis available
        return null;
    }

}
