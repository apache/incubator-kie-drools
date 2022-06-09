package org.optaplanner.core.impl.phase.custom.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class CustomStepScope<Solution_> extends AbstractStepScope<Solution_> {

    private final CustomPhaseScope<Solution_> phaseScope;

    public CustomStepScope(CustomPhaseScope<Solution_> phaseScope) {
        this(phaseScope, phaseScope.getNextStepIndex());
    }

    public CustomStepScope(CustomPhaseScope<Solution_> phaseScope, int stepIndex) {
        super(stepIndex);
        this.phaseScope = phaseScope;
    }

    @Override
    public CustomPhaseScope<Solution_> getPhaseScope() {
        return phaseScope;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}
