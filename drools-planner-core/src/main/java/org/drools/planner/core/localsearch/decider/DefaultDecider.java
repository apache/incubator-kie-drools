/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.localsearch.decider;

import java.util.Iterator;

import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.localsearch.LocalSearchSolverPhase;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.decider.forager.Forager;
import org.drools.planner.core.localsearch.decider.selector.Selector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link Decider}.
 */
public class DefaultDecider implements Decider {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected LocalSearchSolverPhase localSearchSolverPhase;

    protected MoveSelector moveSelector;
    protected Acceptor acceptor;
    protected Forager forager;

    protected boolean assertMoveScoreIsUncorrupted = false;
    protected boolean assertUndoMoveIsUncorrupted = false;

    public void setLocalSearchSolverPhase(LocalSearchSolverPhase localSearchSolverPhase) {
        this.localSearchSolverPhase = localSearchSolverPhase;
    }

    public void setMoveSelector(MoveSelector moveSelector) {
        this.moveSelector = moveSelector;
    }

    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    public Forager getForager() {
        return forager;
    }

    public void setForager(Forager forager) {
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

    public void solvingStarted(DefaultSolverScope solverScope) {
        moveSelector.solvingStarted(solverScope);
        acceptor.solvingStarted(solverScope);
        forager.solvingStarted(solverScope);
    }

    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        moveSelector.phaseStarted(localSearchSolverPhaseScope);
        acceptor.phaseStarted(localSearchSolverPhaseScope);
        forager.phaseStarted(localSearchSolverPhaseScope);
    }

    public void stepStarted(LocalSearchStepScope localSearchStepScope) {
        moveSelector.stepStarted(localSearchStepScope);
        acceptor.stepStarted(localSearchStepScope);
        forager.stepStarted(localSearchStepScope);
    }

    public void decideNextStep(LocalSearchStepScope stepScope) {
        ScoreDirector scoreDirector = stepScope.getScoreDirector();
        int moveIndex = 0;
        for (Move move : moveSelector) {
            MoveScope moveScope = new MoveScope(stepScope);
            moveScope.setMoveIndex(moveIndex);
            moveScope.setMove(move);
            // Filter out not doable moves
            if (move.isMoveDoable(scoreDirector)) {
                doMove(moveScope);
                if (forager.isQuitEarly()) {
                    break;
                }
            } else {
                logger.trace("        Ignoring not doable move ({}).", move);
            }
            moveIndex++;
        }
        MoveScope pickedMoveScope = forager.pickMove(stepScope);
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

    private void doMove(MoveScope moveScope) {
        ScoreDirector scoreDirector = moveScope.getScoreDirector();
        Move move = moveScope.getMove();
        Move undoMove = move.createUndoMove(scoreDirector);
        moveScope.setUndoMove(undoMove);
        move.doMove(scoreDirector);
        processMove(moveScope);
        undoMove.doMove(scoreDirector);
        if (assertUndoMoveIsUncorrupted) {
            LocalSearchSolverPhaseScope localSearchSolverPhaseScope = moveScope.getLocalSearchStepScope()
                    .getLocalSearchSolverPhaseScope();
            Score undoScore = localSearchSolverPhaseScope.calculateScore();
            Score lastCompletedStepScore = localSearchSolverPhaseScope.getLastCompletedStepScope().getScore();
            if (!undoScore.equals(lastCompletedStepScore)) {
                // First assert that are probably no corrupted score rules.
                scoreDirector.assertWorkingScore(undoScore);
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
        logger.trace("        Move score ({}), accepted ({}) for move ({}).",
                new Object[]{moveScope.getScore(), moveScope.getAccepted(), moveScope.getMove()});
    }

    private void processMove(MoveScope moveScope) {
        Score score = moveScope.getLocalSearchStepScope().getLocalSearchSolverPhaseScope().calculateScore();
        if (assertMoveScoreIsUncorrupted) {
            moveScope.getLocalSearchStepScope().getLocalSearchSolverPhaseScope().assertWorkingScore(score);
        }
        moveScope.setScore(score);
        boolean accepted = acceptor.isAccepted(moveScope);
        moveScope.setAccepted(accepted);
        forager.addMove(moveScope);
    }

    public void stepEnded(LocalSearchStepScope localSearchStepScope) {
        moveSelector.stepEnded(localSearchStepScope);
        acceptor.stepEnded(localSearchStepScope);
        forager.stepEnded(localSearchStepScope);
    }

    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        moveSelector.phaseEnded(localSearchSolverPhaseScope);
        acceptor.phaseEnded(localSearchSolverPhaseScope);
        forager.phaseEnded(localSearchSolverPhaseScope);
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        moveSelector.solvingEnded(solverScope);
        acceptor.solvingEnded(solverScope);
        forager.solvingEnded(solverScope);
    }

}
