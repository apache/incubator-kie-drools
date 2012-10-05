package org.drools.planner.core.constructionheuristic.placer.value;

import org.drools.planner.core.constructionheuristic.placer.AbstractPlacer;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicSolverPhaseScope;
import org.drools.planner.core.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.move.generic.ChangeMove;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.localsearch.scope.LocalSearchSolverPhaseScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.termination.Termination;

public class ValuePlacer extends AbstractPlacer {

    protected final Termination termination;
    protected final ValueSelector valueSelector;
    protected final PlanningVariableDescriptor variableDescriptor;
    protected final int selectedCountLimit;

    protected boolean assertMoveScoreIsUncorrupted = false;
    protected boolean assertUndoMoveIsUncorrupted = false;

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

    public void setAssertMoveScoreIsUncorrupted(boolean assertMoveScoreIsUncorrupted) {
        this.assertMoveScoreIsUncorrupted = assertMoveScoreIsUncorrupted;
    }

    public void setAssertUndoMoveIsUncorrupted(boolean assertUndoMoveIsUncorrupted) {
        this.assertUndoMoveIsUncorrupted = assertUndoMoveIsUncorrupted;
    }

    public void doPlacement(ConstructionHeuristicStepScope stepScope) {
        // TODO extract to PlacerForager
        Score maxScore = stepScope.getPhaseScope().getScoreDefinition().getPerfectMinimumScore();
        ConstructionHeuristicMoveScope maxMoveScope = null;

        Object entity = stepScope.getEntity();
        int moveIndex = 0;
        for (Object value : valueSelector) {
            ConstructionHeuristicMoveScope moveScope = new ConstructionHeuristicMoveScope(stepScope);
            moveScope.setMoveIndex(moveIndex);
            ChangeMove move = new ChangeMove(entity, variableDescriptor, value);
            moveScope.setMove(move);
            if (!move.isMoveDoable(stepScope.getScoreDirector())) {
                logger.trace("        Ignoring not doable move ({}).", move);
            } else {
                doMove(moveScope);
                // TODO extract to PlacerForager
                if (moveScope.getScore().compareTo(maxScore) > 0) {
                    maxScore = moveScope.getScore();
                    // TODO for non explicit Best Fit *, default to random picking from a maxMoveScopeList
                    maxMoveScope = moveScope;
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
        if (maxMoveScope != null) {
            Move step = maxMoveScope.getMove();
            stepScope.setStep(step);
            if (logger.isDebugEnabled()) {
                stepScope.setStepString(step.toString());
            }
            stepScope.setUndoStep(maxMoveScope.getUndoMove());
            stepScope.setScore(maxMoveScope.getScore());
        }
    }

    private void doMove(ConstructionHeuristicMoveScope moveScope) {
        ScoreDirector scoreDirector = moveScope.getScoreDirector();
        Move move = moveScope.getMove();
        Move undoMove = move.createUndoMove(scoreDirector);
        moveScope.setUndoMove(undoMove);
        move.doMove(scoreDirector);
        processMove(moveScope);
        undoMove.doMove(scoreDirector);
        if (assertUndoMoveIsUncorrupted) {
            ConstructionHeuristicSolverPhaseScope phaseScope = moveScope.getStepScope().getPhaseScope();
            phaseScope.assertUndoMoveIsUncorrupted(move, undoMove);
        }
        logger.trace("        Move index ({}), score ({}) for move ({}).",
                new Object[]{moveScope.getMoveIndex(), moveScope.getScore(), moveScope.getMove()});
    }

    private void processMove(ConstructionHeuristicMoveScope moveScope) {
        Score score = moveScope.getStepScope().getPhaseScope().calculateScore();
        if (assertMoveScoreIsUncorrupted) {
            moveScope.getStepScope().getPhaseScope().assertWorkingScore(score);
        }
        moveScope.setScore(score);
        // TODO work with forager
        // forager.addMove(moveScope);
    }

}
