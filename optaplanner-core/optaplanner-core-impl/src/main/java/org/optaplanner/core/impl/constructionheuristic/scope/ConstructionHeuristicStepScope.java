package org.optaplanner.core.impl.constructionheuristic.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ConstructionHeuristicStepScope<Solution_> extends AbstractStepScope<Solution_> {

    private final ConstructionHeuristicPhaseScope<Solution_> phaseScope;

    private Object entity = null;
    private Move<Solution_> step = null;
    private String stepString = null;
    private Long selectedMoveCount = null;

    public ConstructionHeuristicStepScope(ConstructionHeuristicPhaseScope<Solution_> phaseScope) {
        this(phaseScope, phaseScope.getNextStepIndex());
    }

    public ConstructionHeuristicStepScope(ConstructionHeuristicPhaseScope<Solution_> phaseScope, int stepIndex) {
        super(stepIndex);
        this.phaseScope = phaseScope;
    }

    @Override
    public ConstructionHeuristicPhaseScope<Solution_> getPhaseScope() {
        return phaseScope;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public Move<Solution_> getStep() {
        return step;
    }

    public void setStep(Move<Solution_> step) {
        this.step = step;
    }

    /**
     * @return null if logging level is too high
     */
    public String getStepString() {
        return stepString;
    }

    public void setStepString(String stepString) {
        this.stepString = stepString;
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

}
