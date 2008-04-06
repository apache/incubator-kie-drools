package org.drools.process.instance.context;

import java.io.Serializable;

import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;

public abstract class AbstractContextInstance implements ContextInstance, Serializable {

    private long contextId;
    private ContextInstanceContainer contextInstanceContainer;
    
    public long getContextId() {
        return contextId;
    }

    public void setContextId(long contextId) {
        this.contextId = contextId;
    }

    public ContextInstanceContainer getContextInstanceContainer() {
        return contextInstanceContainer;
    }

    public void setContextInstanceContainer(ContextInstanceContainer contextInstanceContainer) {
        this.contextInstanceContainer = contextInstanceContainer;
    }
    
    public Context getContext() {
        return getContextInstanceContainer().getContextContainer().getContext(getContextType(), getContextId());
    }
    
}
