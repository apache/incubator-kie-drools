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
import org.drools.planner.core.localsearch.LocalSearchSolver;
import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactory;
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

    protected LocalSearchSolver localSearchSolver;

    protected DeciderScoreComparatorFactory deciderScoreComparatorFactory;
    protected Selector selector;
    protected Acceptor acceptor;
    protected Forager forager;

    protected boolean assertMoveScoreIsUncorrupted = false;
    protected boolean assertUndoMoveIsUncorrupted = false;

    public void setLocalSearchSolver(LocalSearchSolver localSearchSolver) {
        this.localSearchSolver = localSearchSolver;
    }

    public DeciderScoreComparatorFactory getDeciderScoreComparator() {
        return deciderScoreComparatorFactory;
    }

    public void setDeciderScoreComparator(DeciderScoreComparatorFactory deciderScoreComparator) {
        this.deciderScoreComparatorFactory = deciderScoreComparator;
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

    public void solvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        deciderScoreComparatorFactory.solvingStarted(localSearchSolverScope);
        selector.solvingStarted(localSearchSolverScope);
        acceptor.solvingStarted(localSearchSolverScope);
        forager.solvingStarted(localSearchSolverScope);
    }

    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        localSearchStepScope.setDeciderScoreComparator(deciderScoreComparatorFactory.createDeciderScoreComparator());
        selector.beforeDeciding(localSearchStepScope);
        acceptor.beforeDeciding(localSearchStepScope);
        forager.beforeDeciding(localSearchStepScope);
    }

    public void decideNextStep(LocalSearchStepScope localSearchStepScope) {
        WorkingMemory workingMemory = localSearchStepScope.getWorkingMemory();
        Iterator<Move> moveIterator = selector.moveIterator(localSearchStepScope);
        while (moveIterator.hasNext()) {
            Move move = moveIterator.next();
            MoveScope moveScope = new MoveScope(localSearchStepScope);
            moveScope.setMove(move);
            // Filter out not doable moves
            if (move.isMoveDoable(workingMemory)) {
                doMove(moveScope);
                if (forager.isQuitEarly()) {
                    break;
                }
            } else {
                logger.debug("    Ignoring not doable move ({}).", move);
            }
        }
        MoveScope pickedMoveScope = forager.pickMove(localSearchStepScope);
        if (pickedMoveScope != null) {
            Move step = pickedMoveScope.getMove();
            localSearchStepScope.setStep(step);
            localSearchStepScope.setUndoStep(step.createUndoMove(workingMemory));
            localSearchStepScope.setScore(pickedMoveScope.getScore());
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
            Score undoScore = moveScope.getLocalSearchStepScope().getLocalSearchSolverScope().calculateScoreFromWorkingMemory();
            Score lastCompletedStepScore = moveScope.getLocalSearchStepScope().getLocalSearchSolverScope()
                    .getLastCompletedLocalSearchStepScope().getScore();
            if (!undoScore.equals(lastCompletedStepScore)) {
                throw new IllegalStateException(
                        "Corrupted undo move (" + undoMove + ") received from move (" + move + ").\n"
                                + "Unequal lastCompletedStepScore (" + lastCompletedStepScore + ") and undoScore ("
                                + undoScore + ").\n"
                                + moveScope.getLocalSearchStepScope().getLocalSearchSolverScope().buildConstraintOccurrenceSummary());
            }
        }
        logger.debug("    Move score ({}), accept chance ({}) for move ({}).",
                new Object[]{moveScope.getScore(), moveScope.getAcceptChance(), moveScope.getMove()});
    }

    private void processMove(MoveScope moveScope) {
        Score score = moveScope.getLocalSearchStepScope().getLocalSearchSolverScope().calculateScoreFromWorkingMemory();
        if (assertMoveScoreIsUncorrupted) {
            moveScope.getLocalSearchStepScope().getLocalSearchSolverScope().assertWorkingScore(score);
        }
        moveScope.setScore(score);
        double acceptChance = acceptor.calculateAcceptChance(moveScope);
        moveScope.setAcceptChance(acceptChance);
        forager.addMove(moveScope);
    }

    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
        selector.stepDecided(localSearchStepScope);
        acceptor.stepDecided(localSearchStepScope);
        forager.stepDecided(localSearchStepScope);
    }

    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
        selector.stepTaken(localSearchStepScope);
        acceptor.stepTaken(localSearchStepScope);
        forager.stepTaken(localSearchStepScope);
    }

    public void solvingEnded(LocalSearchSolverScope localSearchSolverScope) {
        deciderScoreComparatorFactory.solvingEnded(localSearchSolverScope);
        selector.solvingEnded(localSearchSolverScope);
        acceptor.solvingEnded(localSearchSolverScope);
        forager.solvingEnded(localSearchSolverScope);
    }

}
