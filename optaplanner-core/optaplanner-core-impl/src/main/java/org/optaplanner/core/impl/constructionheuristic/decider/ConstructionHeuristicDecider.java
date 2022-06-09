package org.optaplanner.core.impl.constructionheuristic.decider;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForager;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ConstructionHeuristicDecider<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final String logIndentation;
    protected final Termination<Solution_> termination;
    protected final ConstructionHeuristicForager<Solution_> forager;

    protected boolean assertMoveScoreFromScratch = false;
    protected boolean assertExpectedUndoMoveScore = false;

    public ConstructionHeuristicDecider(String logIndentation, Termination<Solution_> termination,
            ConstructionHeuristicForager<Solution_> forager) {
        this.logIndentation = logIndentation;
        this.termination = termination;
        this.forager = forager;
    }

    public ConstructionHeuristicForager<Solution_> getForager() {
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

    public void solvingStarted(SolverScope<Solution_> solverScope) {
        forager.solvingStarted(solverScope);
    }

    public void phaseStarted(ConstructionHeuristicPhaseScope<Solution_> phaseScope) {
        forager.phaseStarted(phaseScope);
    }

    public void stepStarted(ConstructionHeuristicStepScope<Solution_> stepScope) {
        forager.stepStarted(stepScope);
    }

    public void stepEnded(ConstructionHeuristicStepScope<Solution_> stepScope) {
        forager.stepEnded(stepScope);
    }

    public void phaseEnded(ConstructionHeuristicPhaseScope<Solution_> phaseScope) {
        forager.phaseEnded(phaseScope);
    }

    public void solvingEnded(SolverScope<Solution_> solverScope) {
        forager.solvingEnded(solverScope);
    }

    public void decideNextStep(ConstructionHeuristicStepScope<Solution_> stepScope, Placement<Solution_> placement) {
        int moveIndex = 0;
        for (Move<Solution_> move : placement) {
            ConstructionHeuristicMoveScope<Solution_> moveScope = new ConstructionHeuristicMoveScope<>(stepScope, moveIndex,
                    move);
            moveIndex++;
            // Do not filter out pointless moves, because the original value of the entity(s) is irrelevant.
            // If the original value is null and the variable is nullable, the move to null must be done too.
            doMove(moveScope);
            if (forager.isQuitEarly()) {
                break;
            }
            stepScope.getPhaseScope().getSolverScope().checkYielding();
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
        pickMove(stepScope);
    }

    protected void pickMove(ConstructionHeuristicStepScope<Solution_> stepScope) {
        ConstructionHeuristicMoveScope<Solution_> pickedMoveScope = forager.pickMove(stepScope);
        if (pickedMoveScope != null) {
            Move<Solution_> step = pickedMoveScope.getMove();
            stepScope.setStep(step);
            if (logger.isDebugEnabled()) {
                stepScope.setStepString(step.toString());
            }
            stepScope.setScore(pickedMoveScope.getScore());
        }
    }

    protected <Score_ extends Score<Score_>> void doMove(ConstructionHeuristicMoveScope<Solution_> moveScope) {
        InnerScoreDirector<Solution_, Score_> scoreDirector = moveScope.getScoreDirector();
        scoreDirector.doAndProcessMove(moveScope.getMove(), assertMoveScoreFromScratch, score -> {
            moveScope.setScore(score);
            forager.addMove(moveScope);
        });
        if (assertExpectedUndoMoveScore) {
            scoreDirector.assertExpectedUndoMoveScore(moveScope.getMove(),
                    (Score_) moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore());
        }
        logger.trace("{}        Move index ({}), score ({}), move ({}).",
                logIndentation,
                moveScope.getMoveIndex(), moveScope.getScore(), moveScope.getMove());
    }

}
