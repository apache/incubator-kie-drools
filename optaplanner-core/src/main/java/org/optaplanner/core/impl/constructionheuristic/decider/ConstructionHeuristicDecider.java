package org.optaplanner.core.impl.constructionheuristic.decider;

import java.util.Iterator;

import org.optaplanner.core.impl.constructionheuristic.placer.AbstractPlacer;
import org.optaplanner.core.impl.constructionheuristic.placer.entity.Placement;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicSolverPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.termination.Termination;

public class ConstructionHeuristicDecider extends AbstractPlacer {

    protected final Termination termination;

    protected boolean assertMoveScoreFromScratch = false;
    protected boolean assertExpectedUndoMoveScore = false;

    public ConstructionHeuristicDecider(Termination termination) {
        this.termination = termination;
    }

    public void setAssertMoveScoreFromScratch(boolean assertMoveScoreFromScratch) {
        this.assertMoveScoreFromScratch = assertMoveScoreFromScratch;
    }

    public void setAssertExpectedUndoMoveScore(boolean assertExpectedUndoMoveScore) {
        this.assertExpectedUndoMoveScore = assertExpectedUndoMoveScore;
    }

    public ConstructionHeuristicMoveScope nominateMove(ConstructionHeuristicStepScope stepScope, Placement placement) {
        Score maxScore = null;
        ConstructionHeuristicMoveScope nominatedMoveScope = null;

        int moveIndex = 0;
        for (Move move : placement) {
            ConstructionHeuristicMoveScope moveScope = new ConstructionHeuristicMoveScope(stepScope);
            moveScope.setMoveIndex(moveIndex);
            moveScope.setMove(move);
            // TODO use Selector filtering to filter out not doable moves
            if (!move.isMoveDoable(stepScope.getScoreDirector())) {
                logger.trace("        Move index ({}) not doable, ignoring move ({}).", moveScope.getMoveIndex(), move);
            } else {
                doMove(moveScope);
                if (maxScore == null || moveScope.getScore().compareTo(maxScore) > 0) {
                    maxScore = moveScope.getScore();
                    // TODO for non explicit Best Fit *, default to random picking from a maxMoveScopeList?
                    nominatedMoveScope = moveScope;
                }
            }
            moveIndex++;
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
        stepScope.setSelectedMoveCount((long) moveIndex);
        return nominatedMoveScope;
    }

    private void doMove(ConstructionHeuristicMoveScope moveScope) {
        ScoreDirector scoreDirector = moveScope.getScoreDirector();
        Move move = moveScope.getMove();
        Move undoMove = move.createUndoMove(scoreDirector);
        moveScope.setUndoMove(undoMove);
        move.doMove(scoreDirector);
        processMove(moveScope);
        undoMove.doMove(scoreDirector);
        if (assertExpectedUndoMoveScore) {
            ConstructionHeuristicSolverPhaseScope phaseScope = moveScope.getStepScope().getPhaseScope();
            phaseScope.assertExpectedUndoMoveScore(move, undoMove);
        }
        logger.trace("        Move index ({}), score ({}) for move ({}).",
                moveScope.getMoveIndex(), moveScope.getScore(), moveScope.getMove());
    }

    private void processMove(ConstructionHeuristicMoveScope moveScope) {
        Score score = moveScope.getStepScope().getPhaseScope().calculateScore();
        if (assertMoveScoreFromScratch) {
            moveScope.getStepScope().getPhaseScope().assertWorkingScoreFromScratch(score, moveScope.getMove());
        }
        moveScope.setScore(score);
        // TODO work with forager
        // forager.addMove(moveScope);
    }

}
