package org.drools.core.base;

import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.rule.FactHandle;

public class NonCloningQueryViewListener
    extends AbstractQueryViewListener {

    public FactHandle getHandle(FactHandle originalHandle) {
        return originalHandle;
    }

}
