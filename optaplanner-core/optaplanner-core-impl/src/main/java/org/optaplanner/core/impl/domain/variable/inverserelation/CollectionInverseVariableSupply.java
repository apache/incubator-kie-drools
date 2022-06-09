package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.Collection;

import org.optaplanner.core.impl.domain.variable.supply.Supply;

public interface CollectionInverseVariableSupply extends Supply {

    /**
     * If entity1.varA = x then an inverse of x is entity1.
     *
     * @param planningValue never null
     * @return never null, a {@link Collection} of entities for which the planning variable is the planningValue.
     */
    Collection<?> getInverseCollection(Object planningValue);

}
