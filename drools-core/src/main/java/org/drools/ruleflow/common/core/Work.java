package org.drools.ruleflow.common.core;

import java.util.Map;

public interface Work {

    void setName(String name);
    String getName();
    
    void setParameter(String name, Object value);
    void setParameters(Map parameters);
    Object getParameter(String name);
    Map getParameters();

}
