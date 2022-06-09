package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class ValueRatioTabuSizeStrategy<Solution_> extends AbstractTabuSizeStrategy<Solution_> {

    protected final double tabuRatio;

    public ValueRatioTabuSizeStrategy(double tabuRatio) {
        this.tabuRatio = tabuRatio;
        if (tabuRatio <= 0.0 || tabuRatio >= 1.0) {
            throw new IllegalArgumentException("The tabuRatio (" + tabuRatio
                    + ") must be between 0.0 and 1.0.");
        }
    }

    @Override
    public int determineTabuSize(LocalSearchStepScope<Solution_> stepScope) {
        // TODO we might want to cache the valueCount if and only if moves don't add/remove entities
        int valueCount = stepScope.getPhaseScope().getWorkingValueCount();
        int tabuSize = (int) Math.round(valueCount * tabuRatio);
        return protectTabuSizeCornerCases(valueCount, tabuSize);
    }

}
