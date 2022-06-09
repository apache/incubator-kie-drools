package org.optaplanner.core.impl.domain.variable.inverserelation;

import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * Currently only supported for chained variables and {@link PlanningListVariable list variables},
 * which guarantee that no 2 entities use the same planningValue.
 * <p>
 * To get an instance, demand a {@link SingletonInverseVariableDemand} (for a chained variable)
 * or a {@link SingletonListInverseVariableDemand} (for a list variable) from {@link InnerScoreDirector#getSupplyManager()}.
 */
public interface SingletonInverseVariableSupply extends Supply {

    /**
     * If entity1.varA = x then the inverse of x is entity1.
     *
     * @param planningValue never null
     * @return sometimes null, an entity for which the planning variable is the planningValue.
     */
    Object getInverseSingleton(Object planningValue);

}
