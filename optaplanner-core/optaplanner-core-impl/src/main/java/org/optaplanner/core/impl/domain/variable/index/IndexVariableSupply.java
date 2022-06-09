package org.optaplanner.core.impl.domain.variable.index;

import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * Only supported for {@link PlanningListVariable list variables}.
 * <p>
 * To get an instance, demand an {@link IndexVariableDemand} from {@link InnerScoreDirector#getSupplyManager()}.
 */
public interface IndexVariableSupply extends Supply {

    /**
     * Get {@code planningValue}'s index in the {@link PlanningListVariable list variable} it is an element of.
     *
     * @param planningValue never null
     * @return {@code planningValue}'s index in the list variable it is an element of or {@code null} if the value is unassigned
     */
    Integer getIndex(Object planningValue);
}
