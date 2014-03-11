/*
 * Copyright 2014 JBoss Inc
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
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.optaplanner.core.impl.exhaustivesearch.decider.ExhaustiveSearchDecider;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.DepthFirstNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchSolverPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link ExhaustiveSearchSolverPhase}.
 */
public class DefaultExhaustiveSearchSolverPhase extends AbstractSolverPhase implements ExhaustiveSearchSolverPhase {

    protected EntitySelector entitySelector;
    protected ExhaustiveSearchDecider decider;

    protected boolean assertWorkingSolutionScoreFromScratch = false;
    protected boolean assertExpectedWorkingSolutionScore = false;

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

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        ExhaustiveSearchSolverPhaseScope phaseScope = new ExhaustiveSearchSolverPhaseScope(solverScope);
        phaseStarted(phaseScope);
        SortedSet<ExhaustiveSearchNode> expandableNodeQueue = new TreeSet<ExhaustiveSearchNode>(
                new DepthFirstNodeComparator()); // TODO Do not hardcode type
        phaseScope.setExpandableNodeQueue(expandableNodeQueue);
        fillLayerList(phaseScope);
        initStartNode(phaseScope);

        while (!expandableNodeQueue.isEmpty() && !termination.isPhaseTerminated(phaseScope)) {
            ExhaustiveSearchStepScope stepScope = new ExhaustiveSearchStepScope(phaseScope);
            ExhaustiveSearchNode node = expandableNodeQueue.last();
            stepScope.setExpandingNode(node);
            stepStarted(stepScope);
            restoreWorkingSolution(stepScope);
            decider.expandNode(stepScope);
            stepEnded(stepScope);
            phaseScope.setLastCompletedStepScope(stepScope);
        }
        phaseEnded(phaseScope);
    }

    private void fillLayerList(ExhaustiveSearchSolverPhaseScope phaseScope) {
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
        for (Object entity : entitySelector) {
            ExhaustiveSearchLayer layer = new ExhaustiveSearchLayer(depth, entity);
            layerList.add(layer);
            depth++;
        }
        entitySelector.stepEnded(stepScope);
        phaseScope.setLayerList(layerList);
    }

    private void initStartNode(ExhaustiveSearchSolverPhaseScope phaseScope) {
        ExhaustiveSearchLayer layer = phaseScope.getLayerList().get(0);
        ExhaustiveSearchNode startNode = new ExhaustiveSearchNode(layer, null, 0);
        startNode.setOptimisticBound(phaseScope.getScoreDefinition().getPerfectMaximumScore());
        phaseScope.getExpandableNodeQueue().add(startNode);
        phaseScope.getLastCompletedStepScope().setExpandingNode(startNode);
    }

    protected void restoreWorkingSolution(ExhaustiveSearchStepScope stepScope) {
        ExhaustiveSearchSolverPhaseScope phaseScope = stepScope.getPhaseScope();
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
        phaseScope.getWorkingSolution().setScore(stepScope.getScore());
        if (assertWorkingSolutionScoreFromScratch) {
            phaseScope.assertWorkingScoreFromScratch(stepScope.getScore(), restoreMoveList);
        }
        if (assertExpectedWorkingSolutionScore) {
            phaseScope.assertExpectedWorkingScore(stepScope.getScore(), restoreMoveList);
        }
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        entitySelector.solvingStarted(solverScope);
        decider.solvingStarted(solverScope);
    }

    public void phaseStarted(ExhaustiveSearchSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        entitySelector.phaseStarted(phaseScope);
        decider.phaseStarted(phaseScope);
    }

    public void stepStarted(ExhaustiveSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        // Skip entitySelector.stepStarted(stepScope)
        decider.stepStarted(stepScope);
    }

    public void stepEnded(ExhaustiveSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        // Skip entitySelector.stepEnded(stepScope)
        decider.stepEnded(stepScope);
        if (logger.isDebugEnabled()) {
            long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpent();
            logger.debug("    Step index ({}), time spent ({}), exhaustive depth ({}), score ({}).",
                    stepScope.getStepIndex(), timeMillisSpent,
                    stepScope.getDepth(),
                    stepScope.getScore());
        }
    }

    public void phaseEnded(ExhaustiveSearchSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        entitySelector.phaseEnded(phaseScope);
        decider.phaseEnded(phaseScope);
        logger.info("Phase ({}) ExhaustiveSearch ended: step total ({}), time spent ({}), best score ({}).",
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
