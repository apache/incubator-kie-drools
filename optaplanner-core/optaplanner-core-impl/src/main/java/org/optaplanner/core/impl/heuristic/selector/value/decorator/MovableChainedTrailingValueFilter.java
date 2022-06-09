package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class MovableChainedTrailingValueFilter<Solution_> implements SelectionFilter<Solution_, Object> {

    private final GenuineVariableDescriptor<Solution_> variableDescriptor;

    public MovableChainedTrailingValueFilter(GenuineVariableDescriptor<Solution_> variableDescriptor) {
        this.variableDescriptor = variableDescriptor;
    }

    @Override
    public boolean accept(ScoreDirector<Solution_> scoreDirector, Object value) {
        if (value == null) {
            return true;
        }
        SingletonInverseVariableSupply supply = retrieveSingletonInverseVariableSupply(scoreDirector);
        Object trailingEntity = supply.getInverseSingleton(value);
        EntityDescriptor<Solution_> entityDescriptor = variableDescriptor.getEntityDescriptor();
        if (trailingEntity == null || !entityDescriptor.matchesEntity(trailingEntity)) {
            return true;
        }
        return entityDescriptor.getEffectiveMovableEntitySelectionFilter().accept(scoreDirector, trailingEntity);
    }

    protected SingletonInverseVariableSupply retrieveSingletonInverseVariableSupply(ScoreDirector<Solution_> scoreDirector) {
        // TODO Performance loss because the supply is retrieved for every accept
        // A SelectionFilter should be optionally made aware of lifecycle events, so it can cache the supply
        SupplyManager supplyManager = ((InnerScoreDirector<Solution_, ?>) scoreDirector).getSupplyManager();
        return supplyManager.demand(new SingletonInverseVariableDemand<>(variableDescriptor));
    }

}
