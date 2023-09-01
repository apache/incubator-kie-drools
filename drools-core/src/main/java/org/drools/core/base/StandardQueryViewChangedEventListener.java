package org.drools.core.base;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.rule.FactHandle;

public class StandardQueryViewChangedEventListener
    extends AbstractQueryViewListener {

    public FactHandle getHandle(FactHandle originalHandle) {
        InternalFactHandle fh = (InternalFactHandle) originalHandle;
        // can be null for eval, not and exists that have no right input
        return new DefaultFactHandle( fh.getId(),
                                      fh.getEntryPointId() != null ? fh.getEntryPointId().getEntryPointId() : null,
                                      fh.getIdentityHashCode(),
                                      fh.getObjectHashCode(),
                                      fh.getRecency(),
                                      fh.getObject() );
    }

}
