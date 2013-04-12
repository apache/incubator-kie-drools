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

package org.optaplanner.core.impl.constructionheuristic.greedyFit.decider;

import java.util.Iterator;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.forager.GreedyForager;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitSolverPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitStepScope;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningVariableWalker;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGreedyDecider implements GreedyDecider {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private PlanningVariableWalker planningVariableWalker;
    private GreedyForager forager;

    protected boolean assertMoveScoreIsUncorrupted = false;
    protected boolean assertUndoMoveIsUncorrupted = false;

    public void setPlanningVariableWalker(PlanningVariableWalker planningVariableWalker) {
        this.planningVariableWalker = planningVariableWalker;
    }

    public void setForager(GreedyForager forager) {
        this.forager = forager;
    }

    public void setAssertMoveScoreIsUncorrupted(boolean assertMoveScoreIsUncorrupted) {
        this.assertMoveScoreIsUncorrupted = assertMoveScoreIsUncorrupted;
    }

    public void setAssertUndoMoveIsUncorrupted(boolean assertUndoMoveIsUncorrupted) {
        this.assertUndoMoveIsUncorrupted = assertUndoMoveIsUncorrupted;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void phaseStarted(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        planningVariableWalker.phaseStarted(greedyFitSolverPhaseScope);
        forager.phaseStarted(greedyFitSolverPhaseScope);
    }

    public void stepStarted(GreedyFitStepScope greedyFitStepScope) {
        planningVariableWalker.stepStarted(greedyFitStepScope);
        forager.stepStarted(greedyFitStepScope);
    }

    public void decideNextStep(GreedyFitStepScope stepScope) {
        Object planningEntity = stepScope.getPlanningEntity();
        int moveIndex = 0;
        Iterator<Move> moveIterator = planningVariableWalker.moveIterator(planningEntity);
        while (moveIterator.hasNext()) {
            Move move = moveIterator.next();
            GreedyMoveScope moveScope = new GreedyMoveScope(stepScope);
            moveScope.setMoveIndex(moveIndex);
            moveScope.setMove(move);
            // Filter out not doable moves
            if (move.isMoveDoable(stepScope.getScoreDirector())) {
                doMove(moveScope);
                if (forager.isQuitEarly()) {
                    break;
                }
            } else {
                logger.trace("        Move index ({}) not doable, ignoring move ({}).", moveScope.getMoveIndex(), move);
            }
            moveIndex++;
            // TODO break when terminator returns false. See DefaultDecider
        }
        GreedyMoveScope pickedMoveScope = forager.pickMove(stepScope);
        if (pickedMoveScope != null) {
            Move step = pickedMoveScope.getMove();
            stepScope.setStep(step);
            if (logger.isDebugEnabled()) {
                stepScope.setStepString(step.toString());
            }
            stepScope.setUndoStep(pickedMoveScope.getUndoMove());
            stepScope.setScore(pickedMoveScope.getScore());
        }
    }

    private void doMove(GreedyMoveScope moveScope) {
        ScoreDirector scoreDirector = moveScope.getScoreDirector();
        Move move = moveScope.getMove();
        Move undoMove = move.createUndoMove(scoreDirector);
        moveScope.setUndoMove(undoMove);
        move.doMove(scoreDirector);
        processMove(moveScope);
        undoMove.doMove(scoreDirector);
        if (assertUndoMoveIsUncorrupted) {
            GreedyFitSolverPhaseScope greedyFitSolverPhaseScope = moveScope.getGreedyFitStepScope()
                    .getPhaseScope();
            Score undoScore = greedyFitSolverPhaseScope.calculateScore();
            Score lastCompletedStepScore = greedyFitSolverPhaseScope.getLastCompletedStepScope().getScore();
            if (!undoScore.equals(lastCompletedStepScore)) {
                // First assert that are probably no corrupted score rules.
                greedyFitSolverPhaseScope.getSolverScope().getScoreDirector()
                        .assertWorkingScoreFromScratch(undoScore);
                throw new IllegalStateException(
                        "The moveClass (" + move.getClass() + ")'s move (" + move
                                + ") probably has a corrupted undoMove (" + undoMove + ")." +
                                " Or maybe there are corrupted score rules.\n"
                                + "Check the Move.createUndoMove(...) method of that Move class" +
                                " and enable EnvironmentMode " + EnvironmentMode.FULL_ASSERT
                                + " to fail-faster on corrupted score rules.\n"
                                + "Score corruption: the lastCompletedStepScore (" + lastCompletedStepScore
                                + ") is not the undoScore (" + undoScore + ").");
            }
        }
        logger.trace("        Move index ({}), score ({}) for move ({}).",
                moveScope.getMoveIndex(), moveScope.getScore(), moveScope.getMove());
    }

    private void processMove(GreedyMoveScope moveScope) {
        Score score = moveScope.getGreedyFitStepScope().getPhaseScope().calculateScore();
        if (assertMoveScoreIsUncorrupted) {
            moveScope.getGreedyFitStepScope().getPhaseScope().assertWorkingScoreFromScratch(score);
        }
        moveScope.setScore(score);
        forager.addMove(moveScope);
    }

    public void stepEnded(GreedyFitStepScope greedyFitStepScope) {
        planningVariableWalker.stepEnded(greedyFitStepScope);
        forager.stepEnded(greedyFitStepScope);
    }

    public void phaseEnded(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        planningVariableWalker.phaseEnded(greedyFitSolverPhaseScope);
        forager.phaseEnded(greedyFitSolverPhaseScope);
    }

}
