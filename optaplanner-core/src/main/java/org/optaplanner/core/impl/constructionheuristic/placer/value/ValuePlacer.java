package org.optaplanner.core.impl.constructionheuristic.placer.value;

import java.util.Iterator;

import org.optaplanner.core.impl.constructionheuristic.placer.AbstractPlacer;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicSolverPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.termination.Termination;

public class ValuePlacer extends AbstractPlacer {

    protected final Termination termination;
    protected final ValueSelector valueSelector;
    protected final PlanningVariableDescriptor variableDescriptor;
    protected final int selectedCountLimit;

    protected boolean assertMoveScoreFromScratch = false;
    protected boolean assertExpectedUndoMoveScore = false;

    public ValuePlacer(Termination termination, ValueSelector valueSelector, int selectedCountLimit) {
        this.termination = termination;
        this.valueSelector = valueSelector;
        variableDescriptor = valueSelector.getVariableDescriptor();
        this.selectedCountLimit = selectedCountLimit;
        solverPhaseLifecycleSupport.addEventListener(valueSelector);
        // TODO don't use Integer.MAX_VALUE as a magical value
        if (valueSelector.isNeverEnding() && selectedCountLimit == Integer.MAX_VALUE) {
            throw new IllegalStateException("The placer (" + this
                    + ") with selectedCountLimit (" + selectedCountLimit + ") has valueSelector (" + valueSelector
                    + ") with neverEnding (" + valueSelector.isNeverEnding() + ").");
        }
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    public void setAssertMoveScoreFromScratch(boolean assertMoveScoreFromScratch) {
        this.assertMoveScoreFromScratch = assertMoveScoreFromScratch;
    }

    public void setAssertExpectedUndoMoveScore(boolean assertExpectedUndoMoveScore) {
        this.assertExpectedUndoMoveScore = assertExpectedUndoMoveScore;
    }

    public ConstructionHeuristicMoveScope nominateMove(ConstructionHeuristicStepScope stepScope) {
        Object entity = stepScope.getEntity();
        // If variable should be reinitialized, that happened sooner
        if (variableDescriptor.isInitialized(entity)) {
            return null;
        }
        // TODO extract to PlacerForager
        Score maxScore = null;
        ConstructionHeuristicMoveScope nominatedMoveScope = null;

        int moveIndex = 0;
        for (Iterator it = valueSelector.iterator(entity); it.hasNext(); ) {
            Object value =  it.next();
            ConstructionHeuristicMoveScope moveScope = new ConstructionHeuristicMoveScope(stepScope);
            moveScope.setMoveIndex(moveIndex);
            Move move;
            if (variableDescriptor.isChained()) {
                move = new ChainedChangeMove(entity, variableDescriptor, value);
            } else {
                move = new ChangeMove(entity, variableDescriptor, value);
            }
            moveScope.setMove(move);
            if (!move.isMoveDoable(stepScope.getScoreDirector())) {
                logger.trace("        Move index ({}) not doable, ignoring move ({}).", moveScope.getMoveIndex(), move);
            } else {
                doMove(moveScope);
                // TODO extract to PlacerForager
                if (maxScore == null || moveScope.getScore().compareTo(maxScore) > 0) {
                    maxScore = moveScope.getScore();
                    // TODO for non explicit Best Fit *, default to random picking from a maxMoveScopeList
                    nominatedMoveScope = moveScope;
                }
                if (moveIndex >= selectedCountLimit) {
                    break;
                }
            }
            moveIndex++;
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
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
