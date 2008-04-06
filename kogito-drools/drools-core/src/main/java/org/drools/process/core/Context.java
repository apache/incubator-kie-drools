package org.drools.process.core;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Context {

    String getType();
    
    long getId();
    
    void setId(long id);
    
    Context resolveContext(Object param);
    
}
