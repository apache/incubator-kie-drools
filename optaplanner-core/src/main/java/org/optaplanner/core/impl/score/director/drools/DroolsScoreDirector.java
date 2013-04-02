/*
 * Copyright 2011 JBoss Inc
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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.constraint.ConstraintOccurrence;
import org.optaplanner.core.impl.score.constraint.DoubleConstraintOccurrence;
import org.optaplanner.core.impl.score.constraint.IntConstraintOccurrence;
import org.optaplanner.core.impl.score.constraint.LongConstraintOccurrence;
import org.optaplanner.core.impl.score.constraint.UnweightedConstraintOccurrence;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.solution.Solution;

/**
 * Drools implementation of {@link ScoreDirector}, which directs the Rule Engine to calculate the {@link Score}
 * of the {@link Solution} workingSolution.
 * @see ScoreDirector
 */
public class DroolsScoreDirector extends AbstractScoreDirector<DroolsScoreDirectorFactory> {

    public static final String GLOBAL_SCORE_HOLDER_KEY = "scoreHolder";

    protected KieSession kieSession;
    protected ScoreHolder workingScoreHolder;

    public DroolsScoreDirector(DroolsScoreDirectorFactory scoreDirectorFactory) {
        super(scoreDirectorFactory);
    }

    protected KieBase getKieBase() {
        return scoreDirectorFactory.getKieBase();
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
        kieSession = getKieBase().newKieSession();
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

    // public void beforeEntityAdded(Object entity) // Do nothing

    @Override
    public void afterEntityAdded(Object entity) {
        super.afterEntityAdded(entity);
        if (entity == null) {
            throw new IllegalArgumentException("The entity (" + entity + ") cannot be added to the ScoreDirector.");
        }
        if (!getSolutionDescriptor().hasPlanningEntityDescriptor(entity.getClass())) {
            throw new IllegalArgumentException("The entity (" + entity + ") of class (" + entity.getClass()
                    + ") is not a configured @PlanningEntity.");
        }
        kieSession.insert(entity);
    }

    // public void beforeAllVariablesChanged(Object entity) // Do nothing

    @Override
    public void afterAllVariablesChanged(Object entity) {
        super.afterAllVariablesChanged(entity);
        FactHandle factHandle = kieSession.getFactHandle(entity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The entity (" + entity + ") was never added to this ScoreDirector.");
        }
        kieSession.update(factHandle, entity);
    }

    // public void beforeVariableChanged(Object entity, String variableName) // Do nothing

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        super.afterVariableChanged(entity, variableName);
        FactHandle factHandle = kieSession.getFactHandle(entity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The entity (" + entity + ") was never added to this ScoreDirector.");
        }
        kieSession.update(factHandle, entity);
    }

    // public void beforeEntityRemoved(Object entity) // Do nothing

    @Override
    public void afterEntityRemoved(Object entity) {
        super.afterEntityRemoved(entity);
        FactHandle factHandle = kieSession.getFactHandle(entity);
        if (factHandle == null) {
            throw new IllegalArgumentException("The entity (" + entity + ") was never added to this ScoreDirector.");
        }
        kieSession.retract(factHandle);
    }

    // public void beforeProblemFactAdded(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactAdded(Object problemFact) {
        super.afterProblemFactAdded(problemFact);
        kieSession.insert(problemFact);
    }

    // public void beforeProblemFactChanged(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactChanged(Object problemFact) {
        super.afterProblemFactChanged(problemFact);
        FactHandle factHandle = kieSession.getFactHandle(problemFact);
        if (factHandle == null) {
            throw new IllegalArgumentException("The problemFact (" + problemFact
                    + ") was never added to this ScoreDirector.");
        }
        kieSession.update(factHandle, problemFact);
    }

    // public void beforeProblemFactRemoved(Object problemFact) // Do nothing

    @Override
    public void afterProblemFactRemoved(Object problemFact) {
        super.afterProblemFactRemoved(problemFact);
        FactHandle factHandle = kieSession.getFactHandle(problemFact);
        if (factHandle == null) {
            throw new IllegalArgumentException("The problemFact (" + problemFact
                    + ") was never added to this ScoreDirector.");
        }
        kieSession.retract(factHandle);
    }

    public Score calculateScore() {
        kieSession.fireAllRules();
        Score score = workingScoreHolder.extractScore();
        setCalculatedScore(score);
        return score;
    }

    public boolean isConstraintMatchEnabled() {
        return workingScoreHolder.isConstraintMatchEnabled();
    }

    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        return workingScoreHolder.getConstraintMatchTotals();
    }

    @Override
    public AbstractScoreDirector clone() {
        // TODO experiment with serializing the KieSession to clone it and its entities but not its other facts.
        // See drools-compiler's test SerializationHelper.getSerialisedStatefulKnowledgeSession(...)
        // and use an identity FactFactory that:
        // - returns the reference for a non-@PlanningEntity fact
        // - returns a clone for a @PlanningEntity fact (Pitfall: chained planning entities)
        // Note: currently that will break incremental score calculation, but future drools versions might fix that
        return super.clone();
    }

    @Deprecated // TODO remove in 6.1.0
    @Override
    protected void appendLegacyConstraintOccurrences(StringBuilder analysis,
            ScoreDirector corruptedScoreDirector, ScoreDirector uncorruptedScoreDirector) {
        if (!(uncorruptedScoreDirector instanceof DroolsScoreDirector)) {
            return;
        }
        Set<ConstraintOccurrence> uncorruptedConstraintOccurrenceSet = new LinkedHashSet<ConstraintOccurrence>(
                (Collection<ConstraintOccurrence>) ((DroolsScoreDirector) uncorruptedScoreDirector)
                        .kieSession.getObjects(new ClassObjectFilter(ConstraintOccurrence.class)));
        if (!uncorruptedConstraintOccurrenceSet.isEmpty()) {
            Set<ConstraintOccurrence> corruptedConstraintOccurrenceSet = new LinkedHashSet<ConstraintOccurrence>(
                    (Collection<ConstraintOccurrence>) ((DroolsScoreDirector) corruptedScoreDirector)
                            .kieSession.getObjects(new ClassObjectFilter(ConstraintOccurrence.class)));
            if (corruptedConstraintOccurrenceSet.isEmpty()) {
                analysis.append("  Migration analysis: Corrupted ConstraintMatchTotals:\n");
                for (ConstraintMatchTotal constraintMatchTotal : corruptedScoreDirector.getConstraintMatchTotals()) {
                    analysis.append("    ").append(constraintMatchTotal).append("\n");
                }
            } else {
                analysis.append("  Legacy analysis: Corrupted ConstraintOccurrence totals:\n");
                appendLegacyTotals(analysis, corruptedConstraintOccurrenceSet);
            }
            analysis.append("  Legacy analysis: Uncorrupted ConstraintOccurrence totals:\n");
            appendLegacyTotals(analysis, uncorruptedConstraintOccurrenceSet);
        }
    }

    @Deprecated // TODO remove in 6.1.0
    private void appendLegacyTotals(StringBuilder analysis, Set<ConstraintOccurrence> constraintOccurrenceSet) {
        Map<List<Object>, Double> scoreTotalMap = new LinkedHashMap<List<Object>, Double>();
        for (ConstraintOccurrence constraintOccurrence : constraintOccurrenceSet) {
            List<Object> key = Arrays.<Object>asList(
                    constraintOccurrence.getRuleId(), constraintOccurrence.getConstraintType());
            Double scoreTotal = scoreTotalMap.get(key);
            if (scoreTotal == null) {
                scoreTotal = 0.0;
            }
            double occurrenceScore;
            if (constraintOccurrence instanceof IntConstraintOccurrence) {
                occurrenceScore = ((IntConstraintOccurrence) constraintOccurrence).getWeight();
            } else if (constraintOccurrence instanceof DoubleConstraintOccurrence) {
                occurrenceScore = ((DoubleConstraintOccurrence) constraintOccurrence).getWeight();
            } else if (constraintOccurrence instanceof LongConstraintOccurrence) {
                occurrenceScore = ((LongConstraintOccurrence) constraintOccurrence).getWeight();
            } else if (constraintOccurrence instanceof UnweightedConstraintOccurrence) {
                occurrenceScore = 1.0;
            } else {
                throw new IllegalStateException("Cannot determine occurrenceScore of ConstraintOccurrence class: "
                        + constraintOccurrence.getClass());
            }
            scoreTotal += occurrenceScore;
            scoreTotalMap.put(key, scoreTotal);
        }
        for (Map.Entry<List<Object>, Double> entry : scoreTotalMap.entrySet()) {
            analysis.append("    ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
    }

    @Override
    public void dispose() {
        if (kieSession != null) {
            kieSession.dispose();
            kieSession = null;
        }
    }

}
