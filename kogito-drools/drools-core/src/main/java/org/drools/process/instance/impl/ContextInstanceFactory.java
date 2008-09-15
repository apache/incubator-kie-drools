package org.drools.process.instance.impl;

import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.ProcessInstance;

public interface ContextInstanceFactory {
    
	ContextInstance getContextInstance(Context context, ContextInstanceContainer contextInstanceContainer, ProcessInstance processInstance);
	
}
