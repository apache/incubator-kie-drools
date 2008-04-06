package org.drools.process.core;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Contextable {

    void setContext(String contextType, Context context);
    
    Context getContext(String contextType);
    
}
