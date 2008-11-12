package org.drools.runtime;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author mproctor
 *
 */
public interface GlobalParams {
    void setIn(Map<String, ?> in);
    
    void setOut(Collection<String> out);
    
    void setInOut(Map<String, ?> inOut);
}
