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

package org.drools.planner.core.constructionheuristic.greedyFit.decider;

import java.util.Iterator;

import org.drools.planner.core.constructionheuristic.greedyFit.decider.forager.GreedyForager;
import org.drools.planner.core.heuristic.selector.variable.PlanningVariableWalker;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitSolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitStepScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.director.ScoreDirector;
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

    public void beforeDeciding(GreedyFitStepScope greedyFitStepScope) {
        planningVariableWalker.beforeDeciding(greedyFitStepScope);
        forager.beforeDeciding(greedyFitStepScope);
    }

    public void decideNextStep(GreedyFitStepScope stepScope) {
        Object planningEntity = stepScope.getPlanningEntity();
        Iterator<Move> moveIterator = planningVariableWalker.moveIterator(planningEntity);
        while (moveIterator.hasNext()) {
            Move move = moveIterator.next();
            GreedyMoveScope moveScope = new GreedyMoveScope(stepScope);
            moveScope.setMove(move);
            doMove(moveScope);
            if (forager.isQuitEarly()) {
                break;
            }
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
                    .getGreedyFitSolverPhaseScope();
            Score undoScore = greedyFitSolverPhaseScope.calculateScore();
            Score lastCompletedStepScore = greedyFitSolverPhaseScope.getLastCompletedStepScope().getScore();
            if (!undoScore.equals(lastCompletedStepScore)) {
                // First assert that are probably no corrupted score rules.
                greedyFitSolverPhaseScope.getSolverScope().getScoreDirector()
                        .assertWorkingScore(undoScore);
                throw new IllegalStateException(
                        "The moveClass (" + move.getClass() + ")'s move (" + move
                                + ") probably has a corrupted undoMove (" + undoMove + ")." +
                                " Or maybe there are corrupted score rules.\n"
                                + "Check the Move.createUndoMove(...) method of that Move class" +
                                " and enable EnvironmentMode TRACE to fail-faster on corrupted score rules.\n"
                                + "Score corruption: the lastCompletedStepScore (" + lastCompletedStepScore
                                + ") is not the undoScore (" + undoScore + ").");
            }
        }
        logger.trace("        Move score ({}) for move ({}).",
                new Object[]{moveScope.getScore(), moveScope.getMove()});
    }

    private void processMove(GreedyMoveScope moveScope) {
        Score score = moveScope.getGreedyFitStepScope().getGreedyFitSolverPhaseScope().calculateScore();
        if (assertMoveScoreIsUncorrupted) {
            moveScope.getGreedyFitStepScope().getGreedyFitSolverPhaseScope().assertWorkingScore(score);
        }
        moveScope.setScore(score);
        forager.addMove(moveScope);
    }

    public void stepTaken(GreedyFitStepScope greedyFitStepScope) {
        planningVariableWalker.stepTaken(greedyFitStepScope);
        forager.stepTaken(greedyFitStepScope);
    }

    public void phaseEnded(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        planningVariableWalker.phaseEnded(greedyFitSolverPhaseScope);
        forager.phaseEnded(greedyFitSolverPhaseScope);
    }

}
