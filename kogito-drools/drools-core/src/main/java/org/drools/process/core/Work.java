package org.drools.process.core;

import java.util.Map;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Work {

    void setName(String name);
    String getName();
    
    void setParameter(String name, Object value);
    void setParameters(Map<String, Object> parameters);
    Object getParameter(String name);
    Map<String, Object> getParameters();

}
