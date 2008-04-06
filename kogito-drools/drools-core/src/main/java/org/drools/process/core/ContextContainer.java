package org.drools.process.core;

import java.util.List;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ContextContainer {

    List<Context> getContexts(String contextType);
    
    void addContext(Context context);
    
    Context getContext(String contextType, long id);
    
    void setDefaultContext(Context context);
    
    Context getDefaultContext(String contextType);
}
