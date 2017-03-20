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

package org.optaplanner.core.impl.score.director;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.lookup.ClassAndPlanningIdComparator;
import org.optaplanner.core.impl.domain.lookup.LookUpManager;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerSupport;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.ChildThreadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link ScoreDirector}.
 * <p>
 * Implementation note: Extending classes should follow these guidelines:
 * <ul>
 * <li>before* method: last statement should be a call to the super method</li>
 * <li>after* method: first statement should be a call to the super method</li>
 * </ul>
 * @see ScoreDirector
 */
public abstract class AbstractScoreDirector<Solution_, Factory_ extends AbstractScoreDirectorFactory<Solution_>>
        implements InnerScoreDirector<Solution_>, Cloneable {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final Factory_ scoreDirectorFactory;
    protected final boolean lookUpEnabled;
    protected final LookUpManager lookUpManager;
    protected boolean constraintMatchEnabledPreference;
    protected final VariableListenerSupport<Solution_> variableListenerSupport;

    protected Solution_ workingSolution;
    protected long workingEntityListRevision = 0L;
    protected Integer workingInitScore = null;

    protected boolean allChangesWillBeUndoneBeforeStepEnds = false;

    protected long calculationCount = 0L;

    protected AbstractScoreDirector(Factory_ scoreDirectorFactory,
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        this.lookUpEnabled = lookUpEnabled;
        lookUpManager = lookUpEnabled
                ? new LookUpManager(scoreDirectorFactory.getSolutionDescriptor().getLookUpStrategyResolver()) : null;
        this.constraintMatchEnabledPreference = constraintMatchEnabledPreference;
        variableListenerSupport = new VariableListenerSupport<>(this);
        variableListenerSupport.linkVariableListeners();
    }

    @Override
    public Factory_ getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    @Override
    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return scoreDirectorFactory.getSolutionDescriptor();
    }

    @Override
    public ScoreDefinition getScoreDefinition() {
        return scoreDirectorFactory.getScoreDefinition();
    }

    public boolean isLookUpEnabled() {
        return lookUpEnabled;
    }

    public boolean isConstraintMatchEnabledPreference() {
        return constraintMatchEnabledPreference;
    }

    @Override
    public void overwriteConstraintMatchEnabledPreference(boolean constraintMatchEnabledPreference) {
        this.constraintMatchEnabledPreference = constraintMatchEnabledPreference;
    }

    @Override
    public Solution_ getWorkingSolution() {
        return workingSolution;
    }

    @Override
    public long getWorkingEntityListRevision() {
        return workingEntityListRevision;
    }

    public boolean isAllChangesWillBeUndoneBeforeStepEnds() {
        return allChangesWillBeUndoneBeforeStepEnds;
    }

    @Override
    public void setAllChangesWillBeUndoneBeforeStepEnds(boolean allChangesWillBeUndoneBeforeStepEnds) {
        this.allChangesWillBeUndoneBeforeStepEnds = allChangesWillBeUndoneBeforeStepEnds;
    }

    @Override
    public long getCalculationCount() {
        return calculationCount;
    }

    @Override
    public void resetCalculationCount() {
        this.calculationCount = 0L;
    }

    @Override
    public SupplyManager getSupplyManager() {
        return variableListenerSupport;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public void setWorkingSolution(Solution_ workingSolution) {
        this.workingSolution = workingSolution;
        SolutionDescriptor<Solution_> solutionDescriptor = getSolutionDescriptor();
        workingInitScore = - solutionDescriptor.countUninitializedVariables(workingSolution);
        if (lookUpEnabled) {
            lookUpManager.resetWorkingObjects(solutionDescriptor.getAllFacts(workingSolution));
        }
        variableListenerSupport.resetWorkingSolution();
        setWorkingEntityListDirty();
    }

    @Override
    public boolean isWorkingEntityListDirty(long expectedWorkingEntityListRevision) {
        return workingEntityListRevision != expectedWorkingEntityListRevision;
    }

    protected void setWorkingEntityListDirty() {
        workingEntityListRevision++;
    }

    @Override
    public Solution_ cloneWorkingSolution() {
        return cloneSolution(workingSolution);
    }

    @Override
    public Solution_ cloneSolution(Solution_ originalSolution) {
        SolutionDescriptor<Solution_> solutionDescriptor = getSolutionDescriptor();
        Score originalScore = solutionDescriptor.getScore(originalSolution);
        Solution_ cloneSolution = solutionDescriptor.getSolutionCloner().cloneSolution(originalSolution);
        Score cloneScore = solutionDescriptor.getScore(cloneSolution);
        if (scoreDirectorFactory.isAssertClonedSolution()) {
            if (!Objects.equals(originalScore, cloneScore)) {
                throw new IllegalStateException("Cloning corruption: "
                        + "the original's score (" + originalScore
                        + ") is different from the clone's score (" + cloneScore + ").\n"
                        + "Check the " + SolutionCloner.class.getSimpleName() + ".");
            }
            List<Object> originalEntityList = solutionDescriptor.getEntityList(originalSolution);
            Map<Object, Object> originalEntityMap = new IdentityHashMap<>(originalEntityList.size());
            for (Object originalEntity : originalEntityList) {
                originalEntityMap.put(originalEntity, null);
            }
            for (Object cloneEntity : solutionDescriptor.getEntityList(cloneSolution)) {
                if (originalEntityMap.containsKey(cloneEntity)) {
                    throw new IllegalStateException("Cloning corruption: "
                            + "the same entity (" + cloneEntity
                            + ") is present in both the original and the clone.\n"
                            + "So when a planning variable in the original solution changes, "
                            + "the cloned solution will change too.\n"
                            + "Check the " + SolutionCloner.class.getSimpleName() + ".");
                }
            }
        }
        return cloneSolution;
    }

    @Override
    public int getWorkingEntityCount() {
        return getSolutionDescriptor().getEntityCount(workingSolution);
    }

    @Override
    public List<Object> getWorkingEntityList() {
        return getSolutionDescriptor().getEntityList(workingSolution);
    }

    @Override
    public int getWorkingValueCount() {
        return getSolutionDescriptor().getValueCount(workingSolution);
    }

    @Override
    public void triggerVariableListeners() {
        variableListenerSupport.triggerVariableListenersInNotificationQueues();
    }

    protected void setCalculatedScore(Score score) {
        getSolutionDescriptor().setScore(workingSolution, score);
        calculationCount++;
    }

    @Override
    public AbstractScoreDirector<Solution_, Factory_> clone() {
        // Breaks incremental score calculation.
        // Subclasses should overwrite this method to avoid breaking it if possible.
        AbstractScoreDirector<Solution_, Factory_> clone = (AbstractScoreDirector<Solution_, Factory_>)
                scoreDirectorFactory.buildScoreDirector(isLookUpEnabled(), constraintMatchEnabledPreference);
        clone.setWorkingSolution(cloneWorkingSolution());
        return clone;
    }

    @Override
    public InnerScoreDirector<Solution_> createChildThreadScoreDirector(ChildThreadType childThreadType) {
        AbstractScoreDirector<Solution_, Factory_> childThreadScoreDirector = (AbstractScoreDirector<Solution_, Factory_>)
                scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabledPreference);
        if (childThreadType == ChildThreadType.PART_THREAD) {
            // ScoreCalculationCountTermination takes into account previous phases
            // but the calculationCount of partitions is maxed, not summed.
            childThreadScoreDirector.calculationCount = calculationCount;
        } else {
            throw new IllegalStateException("The childThreadType (" + childThreadType + ") is not implemented.");
        }
        return childThreadScoreDirector;
    }

    @Override
    public void dispose() {
        workingSolution = null;
        workingInitScore = null;
        if (lookUpEnabled) {
            lookUpManager.clearWorkingObjects();
        }
        variableListenerSupport.clearWorkingSolution();
    }

    // ************************************************************************
    // Entity/variable add/change/remove methods
    // ************************************************************************

    @Override
    public final void beforeEntityAdded(Object entity) {
        beforeEntityAdded(getSolutionDescriptor().findEntityDescriptorOrFail(entity.getClass()), entity);
    }

    @Override
    public final void afterEntityAdded(Object entity) {
        afterEntityAdded(getSolutionDescriptor().findEntityDescriptorOrFail(entity.getClass()), entity);
    }

    @Override
    public final void beforeVariableChanged(Object entity, String variableName) {
        VariableDescriptor variableDescriptor = getSolutionDescriptor()
                .findVariableDescriptorOrFail(entity, variableName);
        beforeVariableChanged(variableDescriptor, entity);
    }

    @Override
    public final void afterVariableChanged(Object entity, String variableName) {
        VariableDescriptor variableDescriptor = getSolutionDescriptor()
                .findVariableDescriptorOrFail(entity, variableName);
        afterVariableChanged(variableDescriptor, entity);
    }

    @Override
    public final void beforeEntityRemoved(Object entity) {
        beforeEntityRemoved(getSolutionDescriptor().findEntityDescriptorOrFail(entity.getClass()), entity);
    }

    @Override
    public final void afterEntityRemoved(Object entity) {
        afterEntityRemoved(getSolutionDescriptor().findEntityDescriptorOrFail(entity.getClass()), entity);
    }

    public void beforeEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        variableListenerSupport.beforeEntityAdded(entityDescriptor, entity);
    }

    public void afterEntityAdded(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        workingInitScore -= entityDescriptor.countUninitializedVariables(entity);
        if (lookUpEnabled) {
            lookUpManager.addWorkingObject(entity);
        }
        variableListenerSupport.afterEntityAdded(entityDescriptor, entity);
        if (!allChangesWillBeUndoneBeforeStepEnds) {
            setWorkingEntityListDirty();
        }
    }

    @Override
    public void beforeVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        if (variableDescriptor.isGenuineAndUninitialized(entity)) {
            workingInitScore++;
        }
        variableListenerSupport.beforeVariableChanged(variableDescriptor, entity);
    }

    @Override
    public void afterVariableChanged(VariableDescriptor variableDescriptor, Object entity) {
        if (variableDescriptor.isGenuineAndUninitialized(entity)) {
            workingInitScore--;
        }
        variableListenerSupport.afterVariableChanged(variableDescriptor, entity);
    }

    @Override
    public void changeVariableFacade(VariableDescriptor variableDescriptor, Object entity, Object newValue) {
        beforeVariableChanged(variableDescriptor, entity);
        variableDescriptor.setValue(entity, newValue);
        afterVariableChanged(variableDescriptor, entity);
    }

    public void beforeEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        workingInitScore += entityDescriptor.countUninitializedVariables(entity);
        variableListenerSupport.beforeEntityRemoved(entityDescriptor, entity);
    }

    public void afterEntityRemoved(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        if (lookUpEnabled) {
            lookUpManager.removeWorkingObject(entity);
        }
        variableListenerSupport.afterEntityRemoved(entityDescriptor, entity);
        if (!allChangesWillBeUndoneBeforeStepEnds) {
            setWorkingEntityListDirty();
        }
    }

    // ************************************************************************
    // Problem fact add/change/remove methods
    // ************************************************************************

    @Override
    public void beforeProblemFactAdded(Object problemFact) {
        // Do nothing
    }

    @Override
    public void afterProblemFactAdded(Object problemFact) {
        if (lookUpEnabled) {
            lookUpManager.addWorkingObject(problemFact);
        }
        variableListenerSupport.resetWorkingSolution(); // TODO do not nuke it
    }

    @Override
    public void beforeProblemPropertyChanged(Object problemFactOrEntity) {
        // Do nothing
    }

    @Override
    public void afterProblemPropertyChanged(Object problemFactOrEntity) {
        variableListenerSupport.resetWorkingSolution(); // TODO do not nuke it
    }

    @Override
    public void beforeProblemFactRemoved(Object problemFact) {
        // Do nothing
    }

    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        if (lookUpEnabled) {
            lookUpManager.removeWorkingObject(problemFact);
        }
        variableListenerSupport.resetWorkingSolution(); // TODO do not nuke it
    }

    @Override
    public <E> E lookUpWorkingObject(E externalObject) {
        if (!lookUpEnabled) {
            throw new IllegalStateException("When lookUpEnabled (" + lookUpEnabled
                    + ") is disabled in the constructor, this method should not be called.");
        }
        return lookUpManager.lookUpWorkingObject(externalObject);
    }

    // ************************************************************************
    // Assert methods
    // ************************************************************************

    @Override
    public void assertExpectedWorkingScore(Score expectedWorkingScore, Object completedAction) {
        Score workingScore = calculateScore();
        if (!expectedWorkingScore.equals(workingScore)) {
            throw new IllegalStateException(
                    "Score corruption: the expectedWorkingScore (" + expectedWorkingScore
                    + ") is not the workingScore (" + workingScore
                    + ") after completedAction (" + completedAction + ").");
        }
    }

    @Override
    public void assertShadowVariablesAreNotStale(Score expectedWorkingScore, Object completedAction) {
        SolutionDescriptor<Solution_> solutionDescriptor = getSolutionDescriptor();
        Map<Object, Map<ShadowVariableDescriptor, Object>> entityToShadowVariableValuesMap = new IdentityHashMap<>();
        for (Iterator<Object> it = solutionDescriptor.extractAllEntitiesIterator(workingSolution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor<Solution_> entityDescriptor
                    = solutionDescriptor.findEntityDescriptorOrFail(entity.getClass());
            Collection<ShadowVariableDescriptor<Solution_>> shadowVariableDescriptors = entityDescriptor.getShadowVariableDescriptors();
            Map<ShadowVariableDescriptor, Object> shadowVariableValuesMap
                    = new HashMap<>(shadowVariableDescriptors.size());
            for (ShadowVariableDescriptor shadowVariableDescriptor : shadowVariableDescriptors) {
                Object value = shadowVariableDescriptor.getValue(entity);
                shadowVariableValuesMap.put(shadowVariableDescriptor, value);
            }
            entityToShadowVariableValuesMap.put(entity, shadowVariableValuesMap);
        }
        variableListenerSupport.triggerAllVariableListeners();
        for (Iterator<Object> it = solutionDescriptor.extractAllEntitiesIterator(workingSolution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor<Solution_> entityDescriptor
                    = solutionDescriptor.findEntityDescriptorOrFail(entity.getClass());
            Collection<ShadowVariableDescriptor<Solution_>> shadowVariableDescriptors = entityDescriptor.getShadowVariableDescriptors();
            Map<ShadowVariableDescriptor, Object> shadowVariableValuesMap = entityToShadowVariableValuesMap.get(entity);
            for (ShadowVariableDescriptor shadowVariableDescriptor : shadowVariableDescriptors) {
                Object newValue = shadowVariableDescriptor.getValue(entity);
                Object originalValue = shadowVariableValuesMap.get(shadowVariableDescriptor);
                if (!Objects.equals(originalValue, newValue)) {
                    throw new IllegalStateException(VariableListener.class.getSimpleName() + " corruption:"
                            + " the entity (" + entity
                            + ")'s shadow variable (" + shadowVariableDescriptor.getSimpleEntityAndVariableName()
                            + ")'s corrupted value (" + originalValue + ") changed to uncorrupted value (" + newValue
                            + ") after all " + VariableListener.class.getSimpleName()
                            + "s were triggered without changes to the genuine variables.\n"
                            + "Maybe the " + VariableListener.class.getSimpleName() + " class ("
                            + shadowVariableDescriptor.getVariableListenerClass().getSimpleName()
                            + ") for that shadow variable (" + shadowVariableDescriptor.getSimpleEntityAndVariableName()
                            + ") forgot to update it when one of its sources changed"
                            + " after completedAction (" + completedAction + ").");
                }
            }
        }
        Score workingScore = calculateScore();
        if (!expectedWorkingScore.equals(workingScore)) {
            throw new IllegalStateException("Impossible " + VariableListener.class.getSimpleName() + " corruption:"
                    + " the expectedWorkingScore (" + expectedWorkingScore
                    + ") is not the workingScore (" + workingScore
                    + ") after all " + VariableListener.class.getSimpleName()
                    + "s were triggered without changes to the genuine variables.\n"
                    + "But all the shadow variable values are still the same, so this is impossible.");
        }
    }

    @Override
    public void assertWorkingScoreFromScratch(Score workingScore, Object completedAction) {
        InnerScoreDirectorFactory<Solution_> assertionScoreDirectorFactory
                = scoreDirectorFactory.getAssertionScoreDirectorFactory();
        if (assertionScoreDirectorFactory == null) {
            assertionScoreDirectorFactory = scoreDirectorFactory;
        }
        InnerScoreDirector<Solution_> uncorruptedScoreDirector =
                assertionScoreDirectorFactory.buildScoreDirector(false, true);
        uncorruptedScoreDirector.setWorkingSolution(workingSolution);
        Score uncorruptedScore = uncorruptedScoreDirector.calculateScore();
        if (!workingScore.equals(uncorruptedScore)) {
            String scoreCorruptionAnalysis = buildScoreCorruptionAnalysis(uncorruptedScoreDirector);
            uncorruptedScoreDirector.dispose();
            throw new IllegalStateException(
                    "Score corruption: the workingScore (" + workingScore + ") is not the uncorruptedScore ("
                    + uncorruptedScore + ") after completedAction (" + completedAction
                    + "):\n" + scoreCorruptionAnalysis);
        } else {
            uncorruptedScoreDirector.dispose();
        }
    }

    /**
     * @param uncorruptedScoreDirector never null
     * @return never null
     */
    protected String buildScoreCorruptionAnalysis(ScoreDirector<Solution_> uncorruptedScoreDirector) {
        if (!isConstraintMatchEnabled() || !uncorruptedScoreDirector.isConstraintMatchEnabled()) {
            return "  Score corruption analysis could not be generated because"
                    + " either corrupted constraintMatchEnabled (" + isConstraintMatchEnabled()
                    + ") or uncorrupted constraintMatchEnabled (" + uncorruptedScoreDirector.isConstraintMatchEnabled()
                    + ") is disabled.\n"
                    + "  Check your score constraints manually.";
        }
        Collection<ConstraintMatchTotal> corruptedConstraintMatchTotals = getConstraintMatchTotals();
        Collection<ConstraintMatchTotal> uncorruptedConstraintMatchTotals
                = uncorruptedScoreDirector.getConstraintMatchTotals();

        // The order of justificationLists for score rules that include accumulates isn't stable, so we make it stable.
        ClassAndPlanningIdComparator comparator = new ClassAndPlanningIdComparator(false);
        for (ConstraintMatchTotal constraintMatchTotal : corruptedConstraintMatchTotals) {
            for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
                constraintMatch.getJustificationList().sort(comparator);
            }
        }
        for (ConstraintMatchTotal constraintMatchTotal : uncorruptedConstraintMatchTotals) {
            for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
                constraintMatch.getJustificationList().sort(comparator);
            }
        }

        Map<List<Object>, ConstraintMatch> corruptedMap = createConstraintMatchMap(corruptedConstraintMatchTotals);
        Map<List<Object>, ConstraintMatch> excessMap = new LinkedHashMap<>(corruptedMap);
        Map<List<Object>, ConstraintMatch> missingMap = createConstraintMatchMap(uncorruptedConstraintMatchTotals);
        excessMap.keySet().removeAll(missingMap.keySet()); // missingMap == uncorruptedMap
        missingMap.keySet().removeAll(corruptedMap.keySet());

        final int CONSTRAINT_MATCH_DISPLAY_LIMIT = 8;
        StringBuilder analysis = new StringBuilder();
        if (excessMap.isEmpty()) {
            analysis.append("  The corrupted scoreDirector has no ConstraintMatch(s) which are in excess.\n");
        } else {
            analysis.append("  The corrupted scoreDirector has ").append(excessMap.size())
                    .append(" ConstraintMatch(s) which are in excess (and should not be there):\n");
            int count = 0;
            for (ConstraintMatch constraintMatch : excessMap.values()) {
                if (count >= CONSTRAINT_MATCH_DISPLAY_LIMIT) {
                    analysis.append("    ... ").append(excessMap.size() - CONSTRAINT_MATCH_DISPLAY_LIMIT)
                            .append(" more\n");
                    break;
                }
                analysis.append("    ").append(constraintMatch).append("\n");
                count++;
            }
        }
        if (missingMap.isEmpty()) {
            analysis.append("  The corrupted scoreDirector has no ConstraintMatch(s) which are missing.\n");
        } else {
            analysis.append("  The corrupted scoreDirector has ").append(missingMap.size())
                    .append(" ConstraintMatch(s) which are missing:\n");
            int count = 0;
            for (ConstraintMatch constraintMatch : missingMap.values()) {
                if (count >= CONSTRAINT_MATCH_DISPLAY_LIMIT) {
                    analysis.append("    ... ").append(missingMap.size() - CONSTRAINT_MATCH_DISPLAY_LIMIT)
                            .append(" more\n");
                    break;
                }
                analysis.append("    ").append(constraintMatch).append("\n");
                count++;
            }
        }
        if (excessMap.isEmpty() && missingMap.isEmpty()) {
            analysis.append("  The corrupted scoreDirector has no ConstraintMatch(s) in excess or missing."
                    + " That could be a bug in this class (").append(getClass()).append(").\n");
        }
        analysis.append("  Check your score constraints.");
        return analysis.toString();
    }

    private Map<List<Object>, ConstraintMatch> createConstraintMatchMap(
            Collection<ConstraintMatchTotal> constraintMatchTotals) {
        Map<List<Object>, ConstraintMatch> constraintMatchMap = new LinkedHashMap<>(constraintMatchTotals.size() * 16);
        for (ConstraintMatchTotal constraintMatchTotal : constraintMatchTotals) {
            for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
                ConstraintMatch previousConstraintMatch = constraintMatchMap.put(
                        Arrays.<Object>asList(
                                constraintMatchTotal.getConstraintPackage(),
                                constraintMatchTotal.getConstraintName(),
                                constraintMatch.getJustificationList(),
                                constraintMatch.getScore()),
                        constraintMatch);
                if (previousConstraintMatch != null) {
                    throw new IllegalStateException("Score corruption because the constraintMatch (" + constraintMatch
                            + ") was added twice for constraintMatchTotal (" + constraintMatchTotal
                            + ") without removal.");
                }
            }
        }
        return constraintMatchMap;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + calculationCount + ")";
    }

}
