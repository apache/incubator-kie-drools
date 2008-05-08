package org.drools.process.instance.context.swimlane;

import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.instance.context.AbstractContextInstance;

public abstract class SwimlaneContextInstance extends AbstractContextInstance {

    private static final long serialVersionUID = 400L;

    public String getContextType() {
        return SwimlaneContext.SWIMLANE_SCOPE;
    }
    
    public SwimlaneContext getSwimlaneContext() {
        return (SwimlaneContext) getContext();
    }
    
}
