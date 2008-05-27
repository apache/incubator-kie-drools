package org.drools.process.instance.context.swimlane;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.context.swimlane.Swimlane;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.instance.context.AbstractContextInstance;

public class SwimlaneContextInstance extends AbstractContextInstance {

    private static final long serialVersionUID = 400L;
    
    private Map<String, String> swimlaneActors = new HashMap<String, String>();

    public String getContextType() {
        return SwimlaneContext.SWIMLANE_SCOPE;
    }
    
    public SwimlaneContext getSwimlaneContext() {
        return (SwimlaneContext) getContext();
    }

    public String getActorId(String swimlane) {
        return swimlaneActors.get(swimlane);
    }

    public void setActorId(String swimlane, String actorId) {
        swimlaneActors.put(swimlane, actorId);
    }
    
}
