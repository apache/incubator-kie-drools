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
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.exhaustivesearch.decider.ExhaustiveSearchDecider;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchSolverPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link ExhaustiveSearchSolverPhase}.
 */
public class DefaultExhaustiveSearchSolverPhase extends AbstractSolverPhase implements ExhaustiveSearchSolverPhase {

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

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        SortedSet<ExhaustiveSearchNode> expandableNodeQueue = new TreeSet<ExhaustiveSearchNode>(nodeComparator);
        ExhaustiveSearchSolverPhaseScope phaseScope = new ExhaustiveSearchSolverPhaseScope(solverScope);
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

    public void phaseStarted(ExhaustiveSearchSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        entitySelector.phaseStarted(phaseScope);
        decider.phaseStarted(phaseScope);
        fillLayerList(phaseScope);
        initStartNode(phaseScope);
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
        int entitySizeInt = (int) entitySize;
        List<ExhaustiveSearchLayer> layerList = new ArrayList<ExhaustiveSearchLayer>(entitySizeInt);
        int depth = 0;
        for (Object entity : entitySelector) {
            ExhaustiveSearchLayer layer = new ExhaustiveSearchLayer(depth, entity, entitySizeInt - depth);
            layerList.add(layer);
            depth++;
        }
        if ((entitySizeInt - depth) != 0) {
            throw new IllegalStateException("The entitySelector (" + entitySelector + ")'s size ("
                    + entitySizeInt + ") is not accurate enough for exhaustive search.");
        }
        ExhaustiveSearchLayer layer = new ExhaustiveSearchLayer(depth, null, entitySizeInt - depth);
        layerList.add(layer);
        entitySelector.stepEnded(stepScope);
        phaseScope.setLayerList(layerList);
    }

    private void initStartNode(ExhaustiveSearchSolverPhaseScope phaseScope) {
        ExhaustiveSearchLayer layer = phaseScope.getLayerList().get(0);
        ExhaustiveSearchNode startNode = new ExhaustiveSearchNode(layer, null);

        if (decider.isScoreBounderEnabled()) {
            ScoreDirector scoreDirector = phaseScope.getScoreDirector();
            Score score = scoreDirector.calculateScore();
            startNode.setScore(score);
            ScoreBounder scoreBounder = decider.getScoreBounder();
            phaseScope.setBestPessimisticBound(scoreBounder.calculatePessimisticBound(
                    scoreDirector, score, startNode.getUninitializedVariableCount()));
            startNode.setOptimisticBound(scoreBounder.calculateOptimisticBound(
                    scoreDirector, score, startNode.getUninitializedVariableCount()));
        }

        phaseScope.getExpandableNodeQueue().add(startNode);
        phaseScope.getLastCompletedStepScope().setExpandingNode(startNode);
    }

    public void stepStarted(ExhaustiveSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        // Skip entitySelector.stepStarted(stepScope)
        decider.stepStarted(stepScope);
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

    public void stepEnded(ExhaustiveSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        // Skip entitySelector.stepEnded(stepScope)
        decider.stepEnded(stepScope);
        if (logger.isDebugEnabled()) {
            ExhaustiveSearchSolverPhaseScope phaseScope = stepScope.getPhaseScope();
            long timeMillisSpent = phaseScope.calculateSolverTimeMillisSpent();
            logger.debug("    ES step ({}), time spent ({}), depth ({}), breadth ({}), {} best score ({}), selected move count ({}).",
                    stepScope.getStepIndex(), timeMillisSpent,
                    stepScope.getDepth(),
                    stepScope.getBreadth(),
                    (stepScope.getBestScoreImproved() ? "new" : "   "),
                    phaseScope.getBestScoreWithUninitializedPrefix(),
                    stepScope.getSelectedMoveCount());
        }
    }

    public void phaseEnded(ExhaustiveSearchSolverPhaseScope phaseScope) {
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
