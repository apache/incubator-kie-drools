package org.optaplanner.core.impl.domain.variable.listener;

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Supply;

/**
 * Used to externalize data for a {@link Supply} from the domain model itself.
 */
public interface SourcedVariableListener<Solution_> extends AbstractVariableListener<Solution_, Object>, Supply {

    VariableDescriptor<Solution_> getSourceVariableDescriptor();

}
