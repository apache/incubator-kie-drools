package org.optaplanner.core.impl.partitionedsearch.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class PartitionedSearchStepScope<Solution_> extends AbstractStepScope<Solution_> {

    private final PartitionedSearchPhaseScope<Solution_> phaseScope;

    private PartitionChangeMove<Solution_> step = null;
    private String stepString = null;

    public PartitionedSearchStepScope(PartitionedSearchPhaseScope<Solution_> phaseScope) {
        this(phaseScope, phaseScope.getNextStepIndex());
    }

    public PartitionedSearchStepScope(PartitionedSearchPhaseScope<Solution_> phaseScope, int stepIndex) {
        super(stepIndex);
        this.phaseScope = phaseScope;
    }

    @Override
    public PartitionedSearchPhaseScope<Solution_> getPhaseScope() {
        return phaseScope;
    }

    public PartitionChangeMove<Solution_> getStep() {
        return step;
    }

    public void setStep(PartitionChangeMove<Solution_> step) {
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

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}
