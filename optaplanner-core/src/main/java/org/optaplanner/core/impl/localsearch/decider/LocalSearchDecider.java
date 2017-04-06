/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.forager.Forager;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class LocalSearchDecider<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final String logIndentation;
    protected final Termination termination;
    protected final MoveSelector moveSelector;
    protected final Acceptor acceptor;
    protected final Forager forager;

    protected boolean assertMoveScoreFromScratch = false;
    protected boolean assertExpectedUndoMoveScore = false;

    public LocalSearchDecider(String logIndentation,
            Termination termination, MoveSelector moveSelector, Acceptor acceptor, Forager forager) {
        this.logIndentation = logIndentation;
        this.termination = termination;
        this.moveSelector = moveSelector;
        this.acceptor = acceptor;
        this.forager = forager;
    }

    public Termination getTermination() {
        return termination;
    }

    public MoveSelector getMoveSelector() {
        return moveSelector;
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public Forager getForager() {
        return forager;
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

    public void solvingStarted(DefaultSolverScope<Solution_> solverScope) {
        moveSelector.solvingStarted(solverScope);
        acceptor.solvingStarted(solverScope);
        forager.solvingStarted(solverScope);
    }

    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        moveSelector.phaseStarted(phaseScope);
        acceptor.phaseStarted(phaseScope);
        forager.phaseStarted(phaseScope);
    }

    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        moveSelector.stepStarted(stepScope);
        acceptor.stepStarted(stepScope);
        forager.stepStarted(stepScope);
    }

    public void decideNextStep(LocalSearchStepScope<Solution_> stepScope) {
        InnerScoreDirector<Solution_> scoreDirector = stepScope.getScoreDirector();
        scoreDirector.setAllChangesWillBeUndoneBeforeStepEnds(true);
        int moveIndex = 0;
        for (Move<Solution_> move : moveSelector) {
            LocalSearchMoveScope<Solution_> moveScope = new LocalSearchMoveScope<>(stepScope);
            moveScope.setMoveIndex(moveIndex);
            moveIndex++;
            moveScope.setMove(move);
            // TODO use Selector filtering to filter out not doable moves
            if (!move.isMoveDoable(scoreDirector)) {
                logger.trace("{}        Move index ({}) not doable, ignoring move ({}).",
                        logIndentation, moveScope.getMoveIndex(), move);
            } else {
                doMove(moveScope);
                if (forager.isQuitEarly()) {
                    break;
                }
            }
            stepScope.getPhaseScope().getSolverScope().checkYielding();
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
        scoreDirector.setAllChangesWillBeUndoneBeforeStepEnds(false);
        LocalSearchMoveScope<Solution_> pickedMoveScope = forager.pickMove(stepScope);
        if (pickedMoveScope != null) {
            Move<Solution_> step = pickedMoveScope.getMove();
            stepScope.setStep(step);
            if (logger.isDebugEnabled()) {
                stepScope.setStepString(step.toString());
            }
            stepScope.setUndoStep(pickedMoveScope.getUndoMove());
            stepScope.setScore(pickedMoveScope.getScore());
        }
    }

    private void doMove(LocalSearchMoveScope<Solution_> moveScope) {
        ScoreDirector<Solution_> scoreDirector = moveScope.getScoreDirector();
        Move<Solution_> move = moveScope.getMove();
        Move<Solution_> undoMove = move.doMove(scoreDirector);
        moveScope.setUndoMove(undoMove);
        processMove(moveScope);
        undoMove.doMove(scoreDirector);
        if (assertExpectedUndoMoveScore) {
            LocalSearchPhaseScope<Solution_> phaseScope = moveScope.getStepScope().getPhaseScope();
            phaseScope.assertExpectedUndoMoveScore(move, undoMove, phaseScope.getLastCompletedStepScope().getScore());
        }
        logger.trace("{}        Move index ({}), score ({}), accepted ({}), move ({}).",
                logIndentation,
                moveScope.getMoveIndex(), moveScope.getScore(), moveScope.getAccepted(),
                moveScope.getMove());
    }

    private void processMove(LocalSearchMoveScope<Solution_> moveScope) {
        Score score = moveScope.getStepScope().getPhaseScope().calculateScore();
        if (assertMoveScoreFromScratch) {
            moveScope.getStepScope().getPhaseScope().assertWorkingScoreFromScratch(score, moveScope.getMove());
        }
        moveScope.setScore(score);
        boolean accepted = acceptor.isAccepted(moveScope);
        moveScope.setAccepted(accepted);
        forager.addMove(moveScope);
    }

    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        moveSelector.stepEnded(stepScope);
        acceptor.stepEnded(stepScope);
        forager.stepEnded(stepScope);
    }

    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        moveSelector.phaseEnded(phaseScope);
        acceptor.phaseEnded(phaseScope);
        forager.phaseEnded(phaseScope);
    }

    public void solvingEnded(DefaultSolverScope<Solution_> solverScope) {
        moveSelector.solvingEnded(solverScope);
        acceptor.solvingEnded(solverScope);
        forager.solvingEnded(solverScope);
    }

}
