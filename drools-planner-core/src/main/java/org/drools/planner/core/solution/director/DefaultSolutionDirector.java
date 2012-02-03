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

package org.drools.planner.core.solution.director;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.ClassObjectFilter;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.calculator.ScoreCalculator;
import org.drools.planner.core.score.constraint.ConstraintOccurrence;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link SolutionDirector}.
 * @see SolutionDirector
 */
public class DefaultSolutionDirector implements SolutionDirector {

    public static final String GLOBAL_SCORE_CALCULATOR_KEY = "scoreCalculator";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolutionDescriptor solutionDescriptor;

    protected RuleBase ruleBase;
    protected ScoreDefinition scoreDefinition;

    protected Solution workingSolution;
    protected StatefulSession workingMemory;
    protected ScoreCalculator workingScoreCalculator;

    protected long calculateCount;

    public SolutionDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public void setSolutionDescriptor(SolutionDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    public RuleBase getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDefinition;
    }

    public void setScoreDefinition(ScoreDefinition scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

    public Solution getWorkingSolution() {
        return workingSolution;
    }

    /**
     * The workingSolution must never be the same instance as the bestSolution, it should be a (un)changed clone.
     * @param workingSolution never null
     */
    public void setWorkingSolution(Solution workingSolution) {
        this.workingSolution = workingSolution;
        resetWorkingMemory();
    }

    public WorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public void resetCalculateCount() {
        calculateCount = 0L;
    }

    private void resetWorkingMemory() {
        if (workingMemory != null) {
            workingMemory.dispose();
        }
        workingMemory = ruleBase.newStatefulSession();
        workingScoreCalculator = scoreDefinition.buildScoreCalculator();
        workingMemory.setGlobal(GLOBAL_SCORE_CALCULATOR_KEY, workingScoreCalculator);
        for (Object fact : getWorkingFacts()) {
            workingMemory.insert(fact);
        }
    }

    public Collection<Object> getWorkingFacts() {
        return solutionDescriptor.getAllFacts(workingSolution);
    }

    public List<Object> getWorkingPlanningEntityList() {
        return solutionDescriptor.getPlanningEntityList(workingSolution);
    }

    public boolean isWorkingSolutionInitialized() {
        return solutionDescriptor.isInitialized(workingSolution);
    }

    public Score calculateScoreFromWorkingMemory() {
        workingMemory.fireAllRules();
        Score score = workingScoreCalculator.calculateScore();
        workingSolution.setScore(score);
        calculateCount++;
        return score;
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

    public void disposeWorkingSolution() {
        if (workingMemory != null) {
            workingMemory.dispose();
            workingMemory = null;
        }
    }

    /**
     * @param presumedScore never null
     */
    public void assertWorkingScore(Score presumedScore) {
        DefaultSolutionDirector uncorruptedSolutionDirector = cloneWithoutWorkingSolution();
        uncorruptedSolutionDirector.setWorkingSolution(workingSolution);
        Score uncorruptedScore = uncorruptedSolutionDirector.calculateScoreFromWorkingMemory();
        if (!presumedScore.equals(uncorruptedScore)) {
            String scoreCorruptionAnalysis = buildScoreCorruptionAnalysis(uncorruptedSolutionDirector);
            uncorruptedSolutionDirector.disposeWorkingSolution();
            throw new IllegalStateException(
                    "Score corruption: the presumedScore (" + presumedScore + ") is not the uncorruptedScore ("
                            + uncorruptedScore + "):\n"
                            + scoreCorruptionAnalysis);
        } else {
            uncorruptedSolutionDirector.disposeWorkingSolution();
        }
    }

    public DefaultSolutionDirector cloneWithoutWorkingSolution() {
        DefaultSolutionDirector clone = new DefaultSolutionDirector();
        clone.setSolutionDescriptor(solutionDescriptor);
        clone.setRuleBase(ruleBase);
        clone.setScoreDefinition(scoreDefinition);
        return clone;
    }

    private String buildScoreCorruptionAnalysis(DefaultSolutionDirector uncorruptedSolutionDirector) {
        Set<ConstraintOccurrence> workingConstraintOccurrenceSet = new LinkedHashSet<ConstraintOccurrence>();
        Iterator<ConstraintOccurrence> workingIt = (Iterator<ConstraintOccurrence>)
                workingMemory.iterateObjects(
                new ClassObjectFilter(ConstraintOccurrence.class));
        while (workingIt.hasNext()) {
            workingConstraintOccurrenceSet.add(workingIt.next());
        }
        Set<ConstraintOccurrence> uncorruptedConstraintOccurrenceSet = new LinkedHashSet<ConstraintOccurrence>();
        Iterator<ConstraintOccurrence> uncorruptedIt = (Iterator<ConstraintOccurrence>)
                uncorruptedSolutionDirector.getWorkingMemory().iterateObjects(
                        new ClassObjectFilter(ConstraintOccurrence.class));
        while (uncorruptedIt.hasNext()) {
            uncorruptedConstraintOccurrenceSet.add(uncorruptedIt.next());
        };
        Set<Object> excessSet = new LinkedHashSet<Object>(workingConstraintOccurrenceSet);
        excessSet.removeAll(uncorruptedConstraintOccurrenceSet);
        Set<Object> lackingSet = new LinkedHashSet<Object>(uncorruptedConstraintOccurrenceSet);
        lackingSet.removeAll(workingConstraintOccurrenceSet);

        int CONSTRAINT_OCCURRENCE_DISPLAY_LIMIT = 10;
        StringBuilder analysis = new StringBuilder();
        if (!excessSet.isEmpty()) {
            analysis.append("  The workingMemory has ").append(excessSet.size())
                    .append(" ConstraintOccurrence(s) in excess:\n");
            int count = 0;
            for (Object o : excessSet) {
                if (count >= CONSTRAINT_OCCURRENCE_DISPLAY_LIMIT) {
                    analysis.append("    ... ").append(excessSet.size() - CONSTRAINT_OCCURRENCE_DISPLAY_LIMIT)
                            .append(" more\n");
                    break;
                }
                analysis.append("    ").append(o.toString()).append("\n");
                count++;
            }
        }
        if (!lackingSet.isEmpty()) {
            analysis.append("  The workingMemory has ").append(excessSet.size())
                    .append(" ConstraintOccurrence(s) lacking:\n");
            int count = 0;
            for (Object o : lackingSet) {
                if (count >= CONSTRAINT_OCCURRENCE_DISPLAY_LIMIT) {
                    analysis.append("    ... ").append(lackingSet.size() - CONSTRAINT_OCCURRENCE_DISPLAY_LIMIT)
                            .append(" more\n");
                    break;
                }
                analysis.append("    ").append(o.toString()).append("\n");
                count++;
            }
        }
        if (excessSet.isEmpty() && lackingSet.isEmpty()) {
            analysis.append("  Check the score rules. No ConstraintOccurrence(s) in excess or lacking." +
                    "  Possibly some logically inserted score rules do not extend ConstraintOccurrence.\n" +
                    "  Consider making them extend ConstraintOccurrence" +
                    " or just reuse the build-in ConstraintOccurrence implementations.");

        } else {
            analysis.append("  Check the score rules who created those ConstraintOccurrences." +
                    " Verify that each ConstraintOccurrence's causes and weight is correct.");
        }
        return analysis.toString();
    }

}
