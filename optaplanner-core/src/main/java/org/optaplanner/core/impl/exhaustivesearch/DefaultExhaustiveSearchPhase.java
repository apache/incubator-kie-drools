/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.exhaustivesearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.exhaustivesearch.decider.ExhaustiveSearchDecider;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * Default implementation of {@link ExhaustiveSearchPhase}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class DefaultExhaustiveSearchPhase<Solution_> extends AbstractPhase<Solution_>
        implements ExhaustiveSearchPhase<Solution_> {

    protected Comparator<ExhaustiveSearchNode> nodeComparator;
    protected EntitySelector entitySelector;
    protected ExhaustiveSearchDecider<Solution_> decider;

    protected boolean assertWorkingSolutionScoreFromScratch = false;
    protected boolean assertExpectedWorkingSolutionScore = false;

    public DefaultExhaustiveSearchPhase(int phaseIndex, String logIndentation,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination termination) {
        super(phaseIndex, logIndentation, bestSolutionRecaller, termination);
    }

    public Comparator<ExhaustiveSearchNode> getNodeComparator() {
        return nodeComparator;
    }

    public void setNodeComparator(Comparator<ExhaustiveSearchNode> nodeComparator) {
        this.nodeComparator = nodeComparator;
    }

    public EntitySelector getEntitySelector() {
        return entitySelector;
    }

    public void setEntitySelector(EntitySelector entitySelector) {
        this.entitySelector = entitySelector;
    }

    public ExhaustiveSearchDecider<Solution_> getDecider() {
        return decider;
    }

    public void setDecider(ExhaustiveSearchDecider<Solution_> decider) {
        this.decider = decider;
    }

    public void setAssertWorkingSolutionScoreFromScratch(boolean assertWorkingSolutionScoreFromScratch) {
        this.assertWorkingSolutionScoreFromScratch = assertWorkingSolutionScoreFromScratch;
    }

    public void setAssertExpectedWorkingSolutionScore(boolean assertExpectedWorkingSolutionScore) {
        this.assertExpectedWorkingSolutionScore = assertExpectedWorkingSolutionScore;
    }

    @Override
    public String getPhaseTypeString() {
        return "Exhaustive Search";
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solve(SolverScope<Solution_> solverScope) {
        SortedSet<ExhaustiveSearchNode> expandableNodeQueue = new TreeSet<>(nodeComparator);
        ExhaustiveSearchPhaseScope<Solution_> phaseScope = new ExhaustiveSearchPhaseScope<>(solverScope);
        phaseScope.setExpandableNodeQueue(expandableNodeQueue);
        phaseStarted(phaseScope);

        while (!expandableNodeQueue.isEmpty() && !termination.isPhaseTerminated(phaseScope)) {
            ExhaustiveSearchStepScope<Solution_> stepScope = new ExhaustiveSearchStepScope<>(phaseScope);
            ExhaustiveSearchNode node = expandableNodeQueue.last();
            expandableNodeQueue.remove(node);
            stepScope.setExpandingNode(node);
            stepStarted(stepScope);
            restoreWorkingSolution(stepScope);
            decider.expandNode(stepScope);
            stepEnded(stepScope);
            phaseScope.setLastCompletedStepScope(stepScope);
        }
        phaseEnded(phaseScope);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        entitySelector.solvingStarted(solverScope);
        decider.solvingStarted(solverScope);
    }

    public void phaseStarted(ExhaustiveSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        entitySelector.phaseStarted(phaseScope);
        decider.phaseStarted(phaseScope);
        fillLayerList(phaseScope);
        initStartNode(phaseScope);
    }

    private void fillLayerList(ExhaustiveSearchPhaseScope<Solution_> phaseScope) {
        ExhaustiveSearchStepScope<Solution_> stepScope = new ExhaustiveSearchStepScope<>(phaseScope);
        entitySelector.stepStarted(stepScope);
        long entitySize = entitySelector.getSize();
        if (entitySize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The entitySelector (" + entitySelector
                    + ") has an entitySize (" + entitySize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        List<ExhaustiveSearchLayer> layerList = new ArrayList<>((int) entitySize);
        int depth = 0;
        InnerScoreDirector<Solution_> scoreDirector = phaseScope.getScoreDirector();
        for (Object entity : entitySelector) {
            ExhaustiveSearchLayer layer = new ExhaustiveSearchLayer(depth, entity);
            // Keep in sync with ExhaustiveSearchPhaseConfig.buildMoveSelectorConfig()
            // which includes all genuineVariableDescriptors
            int reinitializeVariableCount = entitySelector.getEntityDescriptor()
                    .countReinitializableVariables(scoreDirector, entity);
            // Ignore entities with only initialized variables to avoid confusing bound decisions
            if (reinitializeVariableCount == 0) {
                continue;
            }
            depth++;
            layerList.add(layer);
        }
        ExhaustiveSearchLayer lastLayer = new ExhaustiveSearchLayer(depth, null);
        layerList.add(lastLayer);
        entitySelector.stepEnded(stepScope);
        phaseScope.setLayerList(layerList);
    }

    private void initStartNode(ExhaustiveSearchPhaseScope<Solution_> phaseScope) {
        ExhaustiveSearchLayer startLayer = phaseScope.getLayerList().get(0);
        ExhaustiveSearchNode startNode = new ExhaustiveSearchNode(startLayer, null);

        if (decider.isScoreBounderEnabled()) {
            InnerScoreDirector<Solution_> scoreDirector = phaseScope.getScoreDirector();
            Score score = scoreDirector.calculateScore();
            startNode.setScore(score);
            ScoreBounder scoreBounder = decider.getScoreBounder();
            phaseScope.setBestPessimisticBound(startLayer.isLastLayer() ? score
                    : scoreBounder.calculatePessimisticBound(scoreDirector, score));
            startNode.setOptimisticBound(startLayer.isLastLayer() ? score
                    : scoreBounder.calculateOptimisticBound(scoreDirector, score));
        }
        if (!startLayer.isLastLayer()) {
            phaseScope.addExpandableNode(startNode);
        }
        phaseScope.getLastCompletedStepScope().setExpandingNode(startNode);
    }

    public void stepStarted(ExhaustiveSearchStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        // Skip entitySelector.stepStarted(stepScope)
        decider.stepStarted(stepScope);
    }

    protected void restoreWorkingSolution(ExhaustiveSearchStepScope<Solution_> stepScope) {
        ExhaustiveSearchPhaseScope<Solution_> phaseScope = stepScope.getPhaseScope();
        ExhaustiveSearchNode oldNode = phaseScope.getLastCompletedStepScope().getExpandingNode();
        ExhaustiveSearchNode newNode = stepScope.getExpandingNode();
        List<Move<Solution_>> oldMoveList = new ArrayList<>(oldNode.getDepth());
        List<Move<Solution_>> newMoveList = new ArrayList<>(newNode.getDepth());
        while (oldNode != newNode) {
            int oldDepth = oldNode.getDepth();
            int newDepth = newNode.getDepth();
            if (oldDepth < newDepth) {
                newMoveList.add(newNode.getMove());
                newNode = newNode.getParent();
            } else {
                oldMoveList.add(oldNode.getUndoMove());
                oldNode = oldNode.getParent();
            }
        }
        List<Move<Solution_>> restoreMoveList = new ArrayList<>(oldMoveList.size() + newMoveList.size());
        restoreMoveList.addAll(oldMoveList);
        Collections.reverse(newMoveList);
        restoreMoveList.addAll(newMoveList);
        InnerScoreDirector<Solution_> scoreDirector = phaseScope.getScoreDirector();
        restoreMoveList.forEach(restoreMove -> restoreMove.doMove(scoreDirector));
        // There is no need to recalculate the score, but we still need to set it
        phaseScope.getSolutionDescriptor().setScore(phaseScope.getWorkingSolution(), stepScope.getStartingStepScore());
        if (assertWorkingSolutionScoreFromScratch) {
            // In BRUTE_FORCE the stepScore can be null because it was not calculated
            if (stepScope.getStartingStepScore() != null) {
                phaseScope.assertPredictedScoreFromScratch(stepScope.getStartingStepScore(), restoreMoveList);
            }
        }
        if (assertExpectedWorkingSolutionScore) {
            // In BRUTE_FORCE the stepScore can be null because it was not calculated
            if (stepScope.getStartingStepScore() != null) {
                phaseScope.assertExpectedWorkingScore(stepScope.getStartingStepScore(), restoreMoveList);
            }
        }
    }

    public void stepEnded(ExhaustiveSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        // Skip entitySelector.stepEnded(stepScope)
        decider.stepEnded(stepScope);
        if (logger.isDebugEnabled()) {
            ExhaustiveSearchPhaseScope<Solution_> phaseScope = stepScope.getPhaseScope();
            logger.debug("{}    ES step ({}), time spent ({}), treeId ({}), {} best score ({}), selected move count ({}).",
                    logIndentation,
                    stepScope.getStepIndex(),
                    phaseScope.calculateSolverTimeMillisSpentUpToNow(),
                    stepScope.getTreeId(),
                    (stepScope.getBestScoreImproved() ? "new" : "   "),
                    phaseScope.getBestScore(),
                    stepScope.getSelectedMoveCount());
        }
    }

    public void phaseEnded(ExhaustiveSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        entitySelector.phaseEnded(phaseScope);
        decider.phaseEnded(phaseScope);
        phaseScope.endingNow();
        logger.info("{}Exhaustive Search phase ({}) ended: time spent ({}), best score ({}),"
                + " score calculation speed ({}/sec), step total ({}).",
                logIndentation,
                phaseIndex,
                phaseScope.calculateSolverTimeMillisSpentUpToNow(),
                phaseScope.getBestScore(),
                phaseScope.getPhaseScoreCalculationSpeed(),
                phaseScope.getNextStepIndex());
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        entitySelector.solvingEnded(solverScope);
        decider.solvingEnded(solverScope);
    }

}
