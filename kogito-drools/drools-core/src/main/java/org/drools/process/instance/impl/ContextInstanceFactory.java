package org.drools.process.instance.impl;

import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;

public interface ContextInstanceFactory {
    
	ContextInstance getContextInstance(Context context, ContextInstanceContainer contextInstanceContainer);
	
}
