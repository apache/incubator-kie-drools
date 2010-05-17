package org.drools.process.core.context.swimlane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Context;
import org.drools.process.core.context.AbstractContext;

public class SwimlaneContext extends AbstractContext {
	
    private static final long serialVersionUID = 400L;

    public static final String SWIMLANE_SCOPE = "SwimlaneScope";
    
	private Map<String, Swimlane> swimlanes = new HashMap<String, Swimlane>();

	public String getType() {
		return SWIMLANE_SCOPE;
	}

    public void addSwimlane(Swimlane swimlane) {
        this.swimlanes.put(swimlane.getName(), swimlane);
    }
    
    public Swimlane getSwimlane(String name) {
        return this.swimlanes.get(name);
    }

    public void removeSwimlane(String name) {
        this.swimlanes.remove(name);
    }
    
    public Collection<Swimlane> getSwimlanes() {
        return new ArrayList<Swimlane>(swimlanes.values());
    }
    
    public void setSwimlanes(Collection<Swimlane> swimlanes) {
        this.swimlanes.clear();
        for (Swimlane swimlane: swimlanes) {
            addSwimlane(swimlane);
        }
    }

	public Context resolveContext(Object param) {
		if (param instanceof String) {
            return getSwimlane((String) param) == null ? null : this;
        }
        throw new IllegalArgumentException(
            "Swimlanes can only resolve swimlane names: " + param);
    }

}
