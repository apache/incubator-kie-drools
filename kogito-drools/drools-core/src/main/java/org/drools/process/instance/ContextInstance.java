package org.drools.process.instance;

import org.drools.process.core.Context;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ContextInstance {
    
    String getContextType();
    
    long getContextId();
    
    ContextInstanceContainer getContextInstanceContainer();
    
    Context getContext();
    
    ProcessInstance getProcessInstance();
    
}
