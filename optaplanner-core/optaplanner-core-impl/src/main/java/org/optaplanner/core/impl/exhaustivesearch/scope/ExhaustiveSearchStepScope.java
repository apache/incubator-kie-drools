package org.optaplanner.core.impl.exhaustivesearch.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ExhaustiveSearchStepScope<Solution_> extends AbstractStepScope<Solution_> {

    private final ExhaustiveSearchPhaseScope<Solution_> phaseScope;

    private ExhaustiveSearchNode expandingNode;
    private Long selectedMoveCount = null;

    public ExhaustiveSearchStepScope(ExhaustiveSearchPhaseScope<Solution_> phaseScope) {
        this(phaseScope, phaseScope.getNextStepIndex());
    }

    public ExhaustiveSearchStepScope(ExhaustiveSearchPhaseScope<Solution_> phaseScope, int stepIndex) {
        super(stepIndex);
        this.phaseScope = phaseScope;
    }

    @Override
    public ExhaustiveSearchPhaseScope<Solution_> getPhaseScope() {
        return phaseScope;
    }

    public ExhaustiveSearchNode getExpandingNode() {
        return expandingNode;
    }

    public void setExpandingNode(ExhaustiveSearchNode expandingNode) {
        this.expandingNode = expandingNode;
    }

    public Score getStartingStepScore() {
        return expandingNode.getScore();
    }

    public Long getSelectedMoveCount() {
        return selectedMoveCount;
    }

    public void setSelectedMoveCount(Long selectedMoveCount) {
        this.selectedMoveCount = selectedMoveCount;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public int getDepth() {
        return expandingNode.getDepth();
    }

    public long getBreadth() {
        return expandingNode.getBreadth();
    }

    public String getTreeId() {
        return expandingNode.getTreeId();
    }

}
