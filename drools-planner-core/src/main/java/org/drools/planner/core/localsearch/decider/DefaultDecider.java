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

import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.LocalSearchSolverPhase;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.decider.forager.Forager;
import org.drools.planner.core.localsearch.decider.selector.Selector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link Decider}.
 */
public class DefaultDecider implements Decider {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected LocalSearchSolverPhase localSearchSolverPhase;

    protected Selector selector;
    protected Acceptor acceptor;
    protected Forager forager;

    protected boolean assertMoveScoreIsUncorrupted = false;
    protected boolean assertUndoMoveIsUncorrupted = false;

    public void setLocalSearchSolverPhase(LocalSearchSolverPhase localSearchSolverPhase) {
        this.localSearchSolverPhase = localSearchSolverPhase;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
        selector.setDecider(this);
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

    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        selector.phaseStarted(localSearchSolverPhaseScope);
        acceptor.phaseStarted(localSearchSolverPhaseScope);
        forager.phaseStarted(localSearchSolverPhaseScope);
    }

    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        selector.beforeDeciding(localSearchStepScope);
        acceptor.beforeDeciding(localSearchStepScope);
        forager.beforeDeciding(localSearchStepScope);
    }

    public void decideNextStep(LocalSearchStepScope stepScope) {
        WorkingMemory workingMemory = stepScope.getWorkingMemory();
        Iterator<Move> moveIterator = selector.moveIterator(stepScope);
        while (moveIterator.hasNext()) {
            Move move = moveIterator.next();
            MoveScope moveScope = new MoveScope(stepScope);
            moveScope.setMove(move);
            // Filter out not doable moves
            if (move.isMoveDoable(workingMemory)) {
                doMove(moveScope);
                if (forager.isQuitEarly()) {
                    break;
                }
            } else {
                logger.trace("        Ignoring not doable move ({}).", move);
            }
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
        WorkingMemory workingMemory = moveScope.getWorkingMemory();
        Move move = moveScope.getMove();
        Move undoMove = move.createUndoMove(workingMemory);
        moveScope.setUndoMove(undoMove);
        move.doMove(workingMemory);
        processMove(moveScope);
        undoMove.doMove(workingMemory);
        if (assertUndoMoveIsUncorrupted) {
            LocalSearchSolverPhaseScope localSearchSolverPhaseScope = moveScope.getLocalSearchStepScope()
                    .getLocalSearchSolverPhaseScope();
            Score undoScore = localSearchSolverPhaseScope.calculateScoreFromWorkingMemory();
            Score lastCompletedStepScore = localSearchSolverPhaseScope.getLastCompletedStepScope().getScore();
            if (!undoScore.equals(lastCompletedStepScore)) {
                // First assert that are probably no corrupted score rules.
                localSearchSolverPhaseScope.getSolverScope().getSolutionDirector()
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
        logger.trace("        Move score ({}), accepted ({}) for move ({}).",
                new Object[]{moveScope.getScore(), moveScope.getAccepted(), moveScope.getMove()});
    }

    private void processMove(MoveScope moveScope) {
        Score score = moveScope.getLocalSearchStepScope().getLocalSearchSolverPhaseScope().calculateScoreFromWorkingMemory();
        if (assertMoveScoreIsUncorrupted) {
            moveScope.getLocalSearchStepScope().getLocalSearchSolverPhaseScope().assertWorkingScore(score);
        }
        moveScope.setScore(score);
        boolean accepted = acceptor.isAccepted(moveScope);
        moveScope.setAccepted(accepted);
        forager.addMove(moveScope);
    }

    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        selector.stepTaken(localSearchStepScope);
        acceptor.stepTaken(localSearchStepScope);
        forager.stepTaken(localSearchStepScope);
    }

    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        selector.phaseEnded(localSearchSolverPhaseScope);
        acceptor.phaseEnded(localSearchSolverPhaseScope);
        forager.phaseEnded(localSearchSolverPhaseScope);
    }

}
