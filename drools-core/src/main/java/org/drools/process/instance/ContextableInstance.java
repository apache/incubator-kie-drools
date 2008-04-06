package org.drools.process.instance;


/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ContextableInstance {
    
    ContextInstance getContextInstance(String contextId);

}
