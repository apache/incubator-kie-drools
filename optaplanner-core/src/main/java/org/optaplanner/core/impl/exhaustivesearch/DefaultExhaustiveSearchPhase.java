/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link ExhaustiveSearchPhase}.
 */
public class DefaultExhaustiveSearchPhase extends AbstractPhase implements ExhaustiveSearchPhase {

    protected Comparator<ExhaustiveSearchNode> nodeComparator;
    protected EntitySelector entitySelector;
    protected ExhaustiveSearchDecider decider;

    protected boolean assertWorkingSolutionScoreFromScratch = false;
    protected boolean assertExpectedWorkingSolutionScore = false;

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

    public ExhaustiveSearchDecider getDecider() {
        return decider;
    }

    public void setDecider(ExhaustiveSearchDecider decider) {
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

    public void solve(DefaultSolverScope solverScope) {
        SortedSet<ExhaustiveSearchNode> expandableNodeQueue = new TreeSet<ExhaustiveSearchNode>(nodeComparator);
        ExhaustiveSearchPhaseScope phaseScope = new ExhaustiveSearchPhaseScope(solverScope);
        phaseScope.setExpandableNodeQueue(expandableNodeQueue);
        phaseStarted(phaseScope);

        while (!expandableNodeQueue.isEmpty() && !termination.isPhaseTerminated(phaseScope)) {
            ExhaustiveSearchStepScope stepScope = new ExhaustiveSearchStepScope(phaseScope);
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
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        entitySelector.solvingStarted(solverScope);
        decider.solvingStarted(solverScope);
    }

    public void phaseStarted(ExhaustiveSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        entitySelector.phaseStarted(phaseScope);
        decider.phaseStarted(phaseScope);
        fillLayerList(phaseScope);
        initStartNode(phaseScope);
    }

    private void fillLayerList(ExhaustiveSearchPhaseScope phaseScope) {
        ExhaustiveSearchStepScope stepScope = new ExhaustiveSearchStepScope(phaseScope);
        entitySelector.stepStarted(stepScope);
        long entitySize = entitySelector.getSize();
        if (entitySize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The entitySelector (" + entitySelector
                    + ") has an entitySize (" + entitySize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        List<ExhaustiveSearchLayer> layerList = new ArrayList<ExhaustiveSearchLayer>((int) entitySize);
        int depth = 0;
        InnerScoreDirector scoreDirector = phaseScope.getScoreDirector();
        int uninitializedVariableCount = phaseScope.getSolutionDescriptor()
                .countReinitializableVariables(scoreDirector, phaseScope.getWorkingSolution());
        for (Object entity : entitySelector) {
            ExhaustiveSearchLayer layer = new ExhaustiveSearchLayer(depth, entity, uninitializedVariableCount);
            // Keep in sync with ExhaustiveSearchPhaseConfig.buildMoveSelectorConfig()
            // which includes all genuineVariableDescriptors
            int reinitializeVariableCount = entitySelector.getEntityDescriptor()
                    .countReinitializableVariables(scoreDirector, entity);
            // Ignore entities with only initialized variables to avoid confusing bound decisions
            if (reinitializeVariableCount == 0) {
                continue;
            }
            depth++;
            uninitializedVariableCount -= reinitializeVariableCount;
            layerList.add(layer);
        }
        ExhaustiveSearchLayer lastLayer = new ExhaustiveSearchLayer(depth, null, uninitializedVariableCount);
        layerList.add(lastLayer);
        entitySelector.stepEnded(stepScope);
        phaseScope.setLayerList(layerList);
    }

    private void initStartNode(ExhaustiveSearchPhaseScope phaseScope) {
        ExhaustiveSearchLayer startLayer = phaseScope.getLayerList().get(0);
        ExhaustiveSearchNode startNode = new ExhaustiveSearchNode(startLayer, null);

        if (decider.isScoreBounderEnabled()) {
            ScoreDirector scoreDirector = phaseScope.getScoreDirector();
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

    public void stepStarted(ExhaustiveSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        // Skip entitySelector.stepStarted(stepScope)
        decider.stepStarted(stepScope);
    }

    protected void restoreWorkingSolution(ExhaustiveSearchStepScope stepScope) {
        ExhaustiveSearchPhaseScope phaseScope = stepScope.getPhaseScope();
        ExhaustiveSearchNode oldNode = phaseScope.getLastCompletedStepScope().getExpandingNode();
        ExhaustiveSearchNode newNode = stepScope.getExpandingNode();
        List<Move> oldMoveList = new ArrayList<Move>(oldNode.getDepth());
        List<Move> newMoveList = new ArrayList<Move>(newNode.getDepth());
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
        List<Move> restoreMoveList = new ArrayList<Move>(oldMoveList.size() + newMoveList.size());
        restoreMoveList.addAll(oldMoveList);
        Collections.reverse(newMoveList);
        restoreMoveList.addAll(newMoveList);
        ScoreDirector scoreDirector = phaseScope.getScoreDirector();
        for (Move restoreMove : restoreMoveList) {
            restoreMove.doMove(scoreDirector);
        }
        // there is no need to recalculate the score, but we still need to set it
        phaseScope.getWorkingSolution().setScore(stepScope.getStartingStepScore());
        if (assertWorkingSolutionScoreFromScratch) {
            // In BRUTE_FORCE the stepScore can be null because it was not calculated
            if (stepScope.getStartingStepScore() != null) {
                phaseScope.assertWorkingScoreFromScratch(stepScope.getStartingStepScore(), restoreMoveList);
            }
        }
        if (assertExpectedWorkingSolutionScore) {
            // In BRUTE_FORCE the stepScore can be null because it was not calculated
            if (stepScope.getStartingStepScore() != null) {
                phaseScope.assertExpectedWorkingScore(stepScope.getStartingStepScore(), restoreMoveList);
            }
        }
    }

    public void stepEnded(ExhaustiveSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        // Skip entitySelector.stepEnded(stepScope)
        decider.stepEnded(stepScope);
        if (logger.isDebugEnabled()) {
            ExhaustiveSearchPhaseScope phaseScope = stepScope.getPhaseScope();
            long timeMillisSpent = phaseScope.calculateSolverTimeMillisSpent();
            logger.debug("    ES step ({}), time spent ({}), treeId ({}), {} best score ({}), selected move count ({}).",
                    stepScope.getStepIndex(), timeMillisSpent,
                    stepScope.getTreeId(),
                    (stepScope.getBestScoreImproved() ? "new" : "   "),
                    phaseScope.getBestScoreWithUninitializedPrefix(),
                    stepScope.getSelectedMoveCount());
        }
    }

    public void phaseEnded(ExhaustiveSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        entitySelector.phaseEnded(phaseScope);
        decider.phaseEnded(phaseScope);
        logger.info("Exhaustive Search phase ({}) ended: step total ({}), time spent ({}), best score ({}).",
                phaseIndex,
                phaseScope.getNextStepIndex(),
                phaseScope.calculateSolverTimeMillisSpent(),
                phaseScope.getBestScore());
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        entitySelector.solvingEnded(solverScope);
        decider.solvingEnded(solverScope);
    }

}
