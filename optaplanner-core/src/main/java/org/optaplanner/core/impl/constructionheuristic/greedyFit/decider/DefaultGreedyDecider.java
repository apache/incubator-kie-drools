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

    protected boolean assertMoveScoreFromScratch = false;
    protected boolean assertExpectedUndoMoveScore = false;

    public void setPlanningVariableWalker(PlanningVariableWalker planningVariableWalker) {
        this.planningVariableWalker = planningVariableWalker;
    }

    public void setForager(GreedyForager forager) {
        this.forager = forager;
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

    public void phaseStarted(GreedyFitSolverPhaseScope phaseScope) {
        planningVariableWalker.phaseStarted(phaseScope);
        forager.phaseStarted(phaseScope);
    }

    public void stepStarted(GreedyFitStepScope stepScope) {
        planningVariableWalker.stepStarted(stepScope);
        forager.stepStarted(stepScope);
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
            // TODO break when terminator returns false. See LocalSearchDecider
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
        if (assertExpectedUndoMoveScore) {
            GreedyFitSolverPhaseScope phaseScope = moveScope.getStepScope()
                    .getPhaseScope();
            phaseScope.assertExpectedUndoMoveScore(move, undoMove);
        }
        logger.trace("        Move index ({}), score ({}) for move ({}).",
                moveScope.getMoveIndex(), moveScope.getScore(), moveScope.getMove());
    }

    private void processMove(GreedyMoveScope moveScope) {
        Score score = moveScope.getStepScope().getPhaseScope().calculateScore();
        if (assertMoveScoreFromScratch) {
            moveScope.getStepScope().getPhaseScope().assertWorkingScoreFromScratch(score, moveScope.getMove());
        }
        moveScope.setScore(score);
        forager.addMove(moveScope);
    }

    public void stepEnded(GreedyFitStepScope stepScope) {
        planningVariableWalker.stepEnded(stepScope);
        forager.stepEnded(stepScope);
    }

    public void phaseEnded(GreedyFitSolverPhaseScope phaseScope) {
        planningVariableWalker.phaseEnded(phaseScope);
        forager.phaseEnded(phaseScope);
    }

}
