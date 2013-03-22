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

package org.optaplanner.core.impl.score.director;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link ScoreDirector}.
 * <p/>
 * Implementation note: Extending classes should follow these guidelines:
 * <ul>
 * <li>before* method: last statement should be a call to the super method</li>
 * <li>after* method: first statement should be a call to the super method</li>
 * </ul>
 * @see ScoreDirector
 */
public abstract class AbstractScoreDirector<F extends AbstractScoreDirectorFactory> implements ScoreDirector {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final F scoreDirectorFactory;

    protected Solution workingSolution;
    protected boolean hasChainedVariables;
    // TODO it's unproven that this caching system is actually faster:
    // it happens for every step for every move, but is only needed for every step (with correction for composite moves)
    protected Map<PlanningVariableDescriptor, Map<Object, Set<Object>>> chainedVariableToTrailingEntitiesMap;

    protected long calculateCount = 0L;

    protected AbstractScoreDirector(F scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        Collection<PlanningVariableDescriptor> chainedVariableDescriptors = getSolutionDescriptor()
                .getChainedVariableDescriptors();
        hasChainedVariables = !chainedVariableDescriptors.isEmpty();
        chainedVariableToTrailingEntitiesMap = new HashMap<PlanningVariableDescriptor, Map<Object, Set<Object>>>(
                chainedVariableDescriptors.size());
        for (PlanningVariableDescriptor chainedVariableDescriptor : chainedVariableDescriptors) {
            chainedVariableToTrailingEntitiesMap.put(chainedVariableDescriptor, null);
        }
    }

    public F getScoreDirectorFactory() {
        return scoreDirectorFactory;
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

    public Solution cloneWorkingSolution() {
        return getSolutionDescriptor().getSolutionCloner().cloneSolution(workingSolution);
    }

    private void resetTrailingEntityMap() {
        if (hasChainedVariables) {
            List<Object> entityList = getSolutionDescriptor().getPlanningEntityList(workingSolution);
            for (Map.Entry<PlanningVariableDescriptor, Map<Object, Set<Object>>> entry
                    : chainedVariableToTrailingEntitiesMap.entrySet()) {
                entry.setValue(new IdentityHashMap<Object, Set<Object>>(entityList.size()));
            }
            // TODO Remove when all starting entities call afterEntityAdded too
            for (Object entity : entityList) {
                insertInTrailingEntityMap(entity);
            }
        }
    }

    private void insertInTrailingEntityMap(Object entity) {
        if (hasChainedVariables) {
            for (Map.Entry<PlanningVariableDescriptor, Map<Object, Set<Object>>> entry
                    : chainedVariableToTrailingEntitiesMap.entrySet()) {
                PlanningVariableDescriptor variableDescriptor = entry.getKey();
                if (variableDescriptor.getPlanningEntityDescriptor().appliesToPlanningEntity(entity)) {
                    Object value = variableDescriptor.getValue(entity);
                    Map<Object, Set<Object>> valueToTrailingEntityMap = entry.getValue();
                    Set<Object> trailingEntities = valueToTrailingEntityMap.get(value);
                    if (trailingEntities == null) {
                        trailingEntities = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
                        valueToTrailingEntityMap.put(value, trailingEntities);
                    }
                    boolean addSucceeded = trailingEntities.add(entity);
                    if (!addSucceeded) {
                        throw new IllegalStateException("The ScoreDirector (" + getClass() + ") is corrupted,"
                                + " because the entity (" + entity + ") for chained planningVariable ("
                                + variableDescriptor.getVariableName()
                                + ") cannot be inserted: it was already inserted.");
                    }
                }
            }
        }
    }

    private void retractFromTrailingEntityMap(Object entity) {
        if (hasChainedVariables) {
            for (Map.Entry<PlanningVariableDescriptor, Map<Object, Set<Object>>> entry
                    : chainedVariableToTrailingEntitiesMap.entrySet()) {
                PlanningVariableDescriptor variableDescriptor = entry.getKey();
                if (variableDescriptor.getPlanningEntityDescriptor().appliesToPlanningEntity(entity)) {
                    Object value = variableDescriptor.getValue(entity);
                    Map<Object, Set<Object>> valueToTrailingEntityMap = entry.getValue();
                    Set<Object> trailingEntities = valueToTrailingEntityMap.get(value);
                    boolean removeSucceeded = trailingEntities != null && trailingEntities.remove(entity);
                    if (!removeSucceeded) {
                        throw new IllegalStateException("The ScoreDirector (" + getClass() + ") is corrupted,"
                                + " because the entity (" + entity + ") for chained planningVariable ("
                                + variableDescriptor.getVariableName()
                                + ") cannot be retracted: it was never inserted.");
                    }
                    if (trailingEntities.isEmpty()) {
                        valueToTrailingEntityMap.put(value, null);
                    }
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

    public int countWorkingSolutionUninitializedVariables() {
        return getSolutionDescriptor().countUninitializedVariables(workingSolution);
    }

    public boolean isWorkingSolutionInitialized() {
        return getSolutionDescriptor().isInitialized(workingSolution);
    }

    protected void setCalculatedScore(Score score) {
        workingSolution.setScore(score);
        calculateCount++;
    }

    public AbstractScoreDirector clone() {
        // Breaks incremental score calculation.
        // Subclasses should overwrite this method to avoid breaking it if possible.
        AbstractScoreDirector clone = (AbstractScoreDirector) scoreDirectorFactory.buildScoreDirector();
        clone.setWorkingSolution(cloneWorkingSolution());
        return clone;
    }

    public Object getTrailingEntity(PlanningVariableDescriptor chainedVariableDescriptor, Object planningValue) {
        Set<Object> trailingEntities = chainedVariableToTrailingEntitiesMap.get(chainedVariableDescriptor)
                .get(planningValue);
        if (trailingEntities == null) {
            return null;
        }
        // trailingEntities can never be an empty list
        if (trailingEntities.size() > 1) {
            throw new IllegalStateException("The planningValue (" + planningValue
                    + ") has multiple trailing entities (" + trailingEntities
                    + ") pointing to it for chained planningVariable ("
                    + chainedVariableDescriptor.getVariableName() + ").");
        }
        return trailingEntities.iterator().next();
    }

    public void assertExpectedWorkingScore(Score expectedWorkingScore) {
        Score workingScore = calculateScore();
        if (!expectedWorkingScore.equals(workingScore)) {
            throw new IllegalStateException(
                    "Score corruption: the expectedWorkingScore (" + expectedWorkingScore
                            + ") is not the workingScore (" + workingScore + ")");

        }
    }

    public void assertWorkingScoreFromScratch(Score workingScore) {
        ScoreDirectorFactory assertionScoreDirectorFactory
                = scoreDirectorFactory.getAssertionScoreDirectorFactory();
        if (assertionScoreDirectorFactory == null) {
            assertionScoreDirectorFactory = scoreDirectorFactory;
        }
        ScoreDirector uncorruptedScoreDirector = assertionScoreDirectorFactory.buildScoreDirector();
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

    public void dispose() {
        // Do nothing
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + calculateCount + ")";
    }

}
