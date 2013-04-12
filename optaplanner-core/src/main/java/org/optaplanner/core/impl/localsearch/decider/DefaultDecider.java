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

package org.optaplanner.core.impl.localsearch.decider;

import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.localsearch.LocalSearchSolverPhase;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.forager.Forager;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link Decider}.
 */
public class DefaultDecider implements Decider {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected LocalSearchSolverPhase localSearchSolverPhase;

    protected Termination termination;
    protected MoveSelector moveSelector;
    protected Acceptor acceptor;
    protected Forager forager;

    protected boolean assertMoveScoreIsUncorrupted = false;
    protected boolean assertUndoMoveIsUncorrupted = false;

    public void setLocalSearchSolverPhase(LocalSearchSolverPhase localSearchSolverPhase) {
        this.localSearchSolverPhase = localSearchSolverPhase;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
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
            LocalSearchMoveScope moveScope = new LocalSearchMoveScope(stepScope);
            moveScope.setMoveIndex(moveIndex);
            moveScope.setMove(move);
            // TODO use Selector filtering to filter out not doable moves
            if (!move.isMoveDoable(scoreDirector)) {
                logger.trace("        Move index ({}) not doable, ignoring move ({}).", moveScope.getMoveIndex(), move);
            } else {
                doMove(moveScope);
                if (forager.isQuitEarly()) {
                    break;
                }
            }
            moveIndex++;
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
        LocalSearchMoveScope pickedMoveScope = forager.pickMove(stepScope);
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

    private void doMove(LocalSearchMoveScope moveScope) {
        ScoreDirector scoreDirector = moveScope.getScoreDirector();
        Move move = moveScope.getMove();
        Move undoMove = move.createUndoMove(scoreDirector);
        moveScope.setUndoMove(undoMove);
        move.doMove(scoreDirector);
        processMove(moveScope);
        undoMove.doMove(scoreDirector);
        if (assertUndoMoveIsUncorrupted) {
            LocalSearchSolverPhaseScope phaseScope = moveScope.getStepScope()
                    .getPhaseScope();
            phaseScope.assertUndoMoveIsUncorrupted(move, undoMove);
        }
        logger.trace("        Move index ({}), score ({}), accepted ({}) for move ({}).",
                moveScope.getMoveIndex(), moveScope.getScore(), moveScope.getAccepted(),
                moveScope.getMove());
    }

    private void processMove(LocalSearchMoveScope moveScope) {
        Score score = moveScope.getStepScope().getPhaseScope().calculateScore();
        if (assertMoveScoreIsUncorrupted) {
            moveScope.getStepScope().getPhaseScope().assertWorkingScoreFromScratch(score);
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
