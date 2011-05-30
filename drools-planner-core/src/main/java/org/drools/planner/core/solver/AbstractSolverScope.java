/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      hhttp://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.solver;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.drools.ClassObjectFilter;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.meta.SolutionDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.calculator.ScoreCalculator;
import org.drools.planner.core.score.constraint.ConstraintOccurrence;
import org.drools.planner.core.score.constraint.DoubleConstraintOccurrence;
import org.drools.planner.core.score.constraint.IntConstraintOccurrence;
import org.drools.planner.core.score.constraint.UnweightedConstraintOccurrence;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSolverScope {

    public static final String GLOBAL_SCORE_CALCULATOR_KEY = "scoreCalculator";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolutionDescriptor solutionDescriptor;

    protected RuleBase ruleBase;
    protected ScoreDefinition scoreDefinition;

    protected long startingSystemTimeMillis;

    protected Solution workingSolution;
    protected StatefulSession workingMemory;
    protected ScoreCalculator workingScoreCalculator;
    protected Random workingRandom;

    protected Score startingScore; // TODO after initialization => ambiguous with setStartingSolution
    protected long calculateCount;

    protected int bestSolutionStepIndex;
    protected Solution bestSolution;
    protected Score bestScore; // TODO remove me

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

    public long getStartingSystemTimeMillis() {
        return startingSystemTimeMillis;
    }

    public void setStartingSystemTimeMillis(long startingSystemTimeMillis) {
        this.startingSystemTimeMillis = startingSystemTimeMillis;
    }

    public Solution getWorkingSolution() {
        return workingSolution;
    }

    public void setWorkingSolution(Solution workingSolution) {
        this.workingSolution = workingSolution;
        resetWorkingMemory();
    }

    public WorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public ScoreCalculator getWorkingScoreCalculator() {
        return workingScoreCalculator;
    }

    public void setWorkingScoreCalculator(ScoreCalculator workingScoreCalculator) {
        this.workingScoreCalculator = workingScoreCalculator;
    }

    public Random getWorkingRandom() {
        return workingRandom;
    }

    public void setWorkingRandom(Random workingRandom) {
        this.workingRandom = workingRandom;
    }

    public Score getStartingScore() {
        return startingScore;
    }

    public void setStartingScore(Score startingScore) {
        this.startingScore = startingScore;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    public int getBestSolutionStepIndex() {
        return bestSolutionStepIndex;
    }

    public void setBestSolutionStepIndex(int bestSolutionStepIndex) {
        this.bestSolutionStepIndex = bestSolutionStepIndex;
    }

    public Solution getBestSolution() {
        return bestSolution;
    }

    public void setBestSolution(Solution bestSolution) {
        this.bestSolution = bestSolution;
    }

    public Score getBestScore() {
        return bestScore;
    }

    public void setBestScore(Score bestScore) {
        this.bestScore = bestScore;
    }

    public abstract AbstractStepScope getLastCompletedAbstractStepScope();

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public void reset() {
        startingSystemTimeMillis = System.currentTimeMillis();
        calculateCount = 0L;
    }

    public Score calculateScoreFromWorkingMemory() {
        workingMemory.fireAllRules();
        Score score = workingScoreCalculator.calculateScore();
        workingSolution.setScore(score);
        calculateCount++;
        return score;
    }

    public long calculateTimeMillisSpend() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

    private void resetWorkingMemory() {
        if (workingMemory != null) {
            workingMemory.dispose();
        }
        workingMemory = ruleBase.newStatefulSession();
        workingMemory.setGlobal(GLOBAL_SCORE_CALCULATOR_KEY, workingScoreCalculator);
        for (Object fact : getWorkingFacts()) {
            workingMemory.insert(fact);
        }
    }

    public Collection<Object> getWorkingFacts() {
        return solutionDescriptor.getAllFacts(workingSolution);
    }

    public Collection<Object> getWorkingPlanningEntities() {
        return solutionDescriptor.getPlanningEntities(workingSolution);
    }

    /**
     * @param presumedScore never null
     */
    public void assertWorkingScore(Score presumedScore) {
        StatefulSession tmpWorkingMemory = ruleBase.newStatefulSession();
        ScoreCalculator tmpScoreCalculator = workingScoreCalculator.clone();
        tmpWorkingMemory.setGlobal(GLOBAL_SCORE_CALCULATOR_KEY, tmpScoreCalculator);
        for (Object fact : getWorkingFacts()) {
            tmpWorkingMemory.insert(fact);
        }
        tmpWorkingMemory.fireAllRules();
        Score realScore = tmpScoreCalculator.calculateScore();
        tmpWorkingMemory.dispose();
        if (!presumedScore.equals(realScore)) {
            throw new IllegalStateException(
                    "The presumedScore (" + presumedScore + ") is corrupted because it is not the realScore  ("
                            + realScore + ").\n"
                            + "Presumed workingMemory:\n" + buildConstraintOccurrenceSummary(workingMemory)
                            + "Real workingMemory:\n" + buildConstraintOccurrenceSummary(tmpWorkingMemory));
        }
    }

    /**
     * Calls {@link #buildConstraintOccurrenceSummary(WorkingMemory)} with the current {@link #workingMemory}.
     * @return never null
     */
    public String buildConstraintOccurrenceSummary() {
        return buildConstraintOccurrenceSummary(workingMemory);
    }

    /**
     * TODO Refactor this with the ConstraintOccurrenceTotal class: https://jira.jboss.org/jira/browse/JBRULES-2510
     * @param summaryWorkingMemory sometimes null
     * @return never null
     */
    public String buildConstraintOccurrenceSummary(WorkingMemory summaryWorkingMemory) {
        logger.trace("Building ConstraintOccurrence summary");
        if (summaryWorkingMemory == null) {
            return "  The workingMemory is null.";
        }
        Map<String, SummaryLine> summaryLineMap = new TreeMap<String, SummaryLine>();
        Iterator<ConstraintOccurrence> it = (Iterator<ConstraintOccurrence>) summaryWorkingMemory.iterateObjects(
                new ClassObjectFilter(ConstraintOccurrence.class));
        while (it.hasNext()) {
            ConstraintOccurrence occurrence = it.next();
            logger.trace("Adding ConstraintOccurrence ({})", occurrence);
            SummaryLine summaryLine = summaryLineMap.get(occurrence.getRuleId());
            if (summaryLine == null) {
                summaryLine = new SummaryLine();
                summaryLineMap.put(occurrence.getRuleId(), summaryLine);
            }
            summaryLine.increment();
            if (occurrence instanceof IntConstraintOccurrence) {
                summaryLine.addWeight(((IntConstraintOccurrence) occurrence).getWeight());
            } else if (occurrence instanceof DoubleConstraintOccurrence) {
                summaryLine.addWeight(((DoubleConstraintOccurrence) occurrence).getWeight());
            } else if (occurrence instanceof UnweightedConstraintOccurrence) {
                summaryLine.addWeight(1);
            } else {
                throw new IllegalStateException("Cannot determine occurrenceScore of ConstraintOccurrence class: "
                        + occurrence.getClass());
            }
        }
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, SummaryLine> summaryLineEntry : summaryLineMap.entrySet()) {
            SummaryLine summaryLine = summaryLineEntry.getValue();
            summary.append("  Score rule (").append(summaryLineEntry.getKey()).append(") has count (")
                    .append(summaryLine.getCount()).append(") and weight total (")
                    .append(summaryLine.getWeightTotal()).append(").\n");
        }
        return summary.toString();
    }

    private class SummaryLine {
        private int count = 0;
        private Number weightTotal = null;

        public int getCount() {
            return count;
        }

        public Number getWeightTotal() {
            return weightTotal;
        }

        public void increment() {
            count++;
        }

        public void addWeight(Integer weight) {
            if (weightTotal == null) {
                weightTotal = 0;
            }
            weightTotal = ((Integer) weightTotal) + weight;
        }

        public void addWeight(Double weight) {
            if (weightTotal == null) {
                weightTotal = 0.0;
            }
            weightTotal = ((Double) weightTotal) + weight;
        }
    }

}
