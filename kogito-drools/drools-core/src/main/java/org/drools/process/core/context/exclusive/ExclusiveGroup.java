package org.drools.process.core.context.exclusive;

import org.drools.process.core.Context;
import org.drools.process.core.context.AbstractContext;

public class ExclusiveGroup extends AbstractContext {
	
    private static final long serialVersionUID = 400L;

    public static final String EXCLUSIVE_GROUP = "ExclusiveGroup";
    
	public String getType() {
		return EXCLUSIVE_GROUP;
	}

	public Context resolveContext(Object param) {
		return null;
    }

}
