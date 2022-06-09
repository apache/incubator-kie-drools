package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class EntityRatioTabuSizeStrategy<Solution_> extends AbstractTabuSizeStrategy<Solution_> {

    protected final double tabuRatio;

    public EntityRatioTabuSizeStrategy(double tabuRatio) {
        this.tabuRatio = tabuRatio;
        if (tabuRatio <= 0.0 || tabuRatio >= 1.0) {
            throw new IllegalArgumentException("The tabuRatio (" + tabuRatio
                    + ") must be between 0.0 and 1.0.");
        }
    }

    @Override
    public int determineTabuSize(LocalSearchStepScope<Solution_> stepScope) {
        // TODO we might want to cache the entityCount if and only if moves don't add/remove entities
        int entityCount = stepScope.getPhaseScope().getWorkingEntityCount();
        int tabuSize = (int) Math.round(entityCount * tabuRatio);
        return protectTabuSizeCornerCases(entityCount, tabuSize);
    }

}
