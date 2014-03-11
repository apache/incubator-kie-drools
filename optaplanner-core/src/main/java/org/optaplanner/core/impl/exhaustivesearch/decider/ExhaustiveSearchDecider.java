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

package org.optaplanner.core.impl.exhaustivesearch.decider;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchSolverPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.ManualEntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExhaustiveSearchDecider {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final BestSolutionRecaller bestSolutionRecaller;
    protected final Termination termination;
    protected final ManualEntityMimicRecorder manualEntityMimicRecorder;
    protected final MoveSelector moveSelector;
    protected final ScoreBounder scoreBounder;

    protected boolean assertMoveScoreFromScratch = false;
    protected boolean assertExpectedUndoMoveScore = false;

    public ExhaustiveSearchDecider(BestSolutionRecaller bestSolutionRecaller, Termination termination,
            ManualEntityMimicRecorder manualEntityMimicRecorder, MoveSelector moveSelector, ScoreBounder scoreBounder) {
        this.bestSolutionRecaller = bestSolutionRecaller;
        this.termination = termination;
        this.manualEntityMimicRecorder = manualEntityMimicRecorder;
        this.moveSelector = moveSelector;
        this.scoreBounder = scoreBounder;
    }

    public MoveSelector getMoveSelector() {
        return moveSelector;
    }

    public void setAssertMoveScoreFromScratch(boolean assertMoveScoreFromScratch) {
        this.assertMoveScoreFromScratch = assertMoveScoreFromScratch;
    }

    public void setAssertExpectedUndoMoveScore(boolean assertExpectedUndoMoveScore) {
        this.assertExpectedUndoMoveScore = assertExpectedUndoMoveScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solvingStarted(DefaultSolverScope solverScope) {
        moveSelector.solvingStarted(solverScope);
    }

    public void phaseStarted(ExhaustiveSearchSolverPhaseScope phaseScope) {
        moveSelector.phaseStarted(phaseScope);
    }

    public void stepStarted(ExhaustiveSearchStepScope stepScope) {
        moveSelector.stepStarted(stepScope);
    }

    public void stepEnded(ExhaustiveSearchStepScope stepScope) {
        moveSelector.stepEnded(stepScope);
    }

    public void phaseEnded(ExhaustiveSearchSolverPhaseScope phaseScope) {
        moveSelector.phaseEnded(phaseScope);
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        moveSelector.solvingEnded(solverScope);
    }

    public void expandNode(ExhaustiveSearchStepScope stepScope) {
        ExhaustiveSearchNode expandingNode = stepScope.getExpandingNode();
        manualEntityMimicRecorder.setRecordedEntity(expandingNode.getEntity());

        ExhaustiveSearchLayer moveLayer = stepScope.getPhaseScope().getLayerList().get(expandingNode.getDepth() + 1);
        for (Move move : moveSelector) {
            ExhaustiveSearchNode moveNode = new ExhaustiveSearchNode(moveLayer, expandingNode,
                    moveLayer.assignIndexInLayer());
            moveNode.setMove(move);
            // Do not filter out pointless moves, because the original value of the entity(s) is irrelevant.
            // If the original value is null and the variable is nullable, the move to null must be done too.
            doMove(stepScope, moveNode);
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
    }

    private void doMove(ExhaustiveSearchStepScope stepScope, ExhaustiveSearchNode moveNode) {
        ScoreDirector scoreDirector = stepScope.getScoreDirector();
        Move move = moveNode.getMove();
        Move undoMove = move.createUndoMove(scoreDirector);
        moveNode.setUndoMove(undoMove);
        move.doMove(scoreDirector);
        processMove(stepScope, moveNode);
        undoMove.doMove(scoreDirector);
        if (assertExpectedUndoMoveScore) {
            ExhaustiveSearchSolverPhaseScope phaseScope = stepScope.getPhaseScope();
            phaseScope.assertExpectedUndoMoveScore(move, undoMove);
        }
        logger.trace("        Move indexInLayer ({}), score ({}) for move ({}).",
                moveNode.getIndexInLayer(), moveNode.getScore(), moveNode.getMove());
    }

    private void processMove(ExhaustiveSearchStepScope stepScope, ExhaustiveSearchNode moveNode) {
        ExhaustiveSearchSolverPhaseScope phaseScope = stepScope.getPhaseScope();
        Score score = phaseScope.calculateScore();
        if (assertMoveScoreFromScratch) {
            phaseScope.assertWorkingScoreFromScratch(score, moveNode.getMove());
        }
        moveNode.setScore(score);
        int uninitializedVariableCount = phaseScope.getDepthSize() - moveNode.getDepth();
        if (uninitializedVariableCount == 0) {
            // There is no point in bounding a fully initialized score
            phaseScope.registerPessimisticBound(score);
            bestSolutionRecaller.processWorkingSolutionDuringMove(uninitializedVariableCount, score, stepScope);
        } else {
            Score optimisticBound = scoreBounder.calculateOptimisticBound(phaseScope.getScoreDirector(), score,
                    uninitializedVariableCount);
            moveNode.setOptimisticBound(optimisticBound);
            if (optimisticBound.compareTo(phaseScope.getBestPessimisticBound()) > 0) {
                // It's still worth investigating this node further (no need to prune it)
                phaseScope.getExpandableNodeQueue().add(moveNode);
                Score pessimisticBound = scoreBounder.calculatePessimisticBound(phaseScope.getScoreDirector(), score,
                        uninitializedVariableCount);
                phaseScope.registerPessimisticBound(pessimisticBound);
            }
        }
    }

}
