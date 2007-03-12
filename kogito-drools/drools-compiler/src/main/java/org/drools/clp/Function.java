package org.drools.clp;

import java.util.Map;

public interface Function extends ValueHandler {
    public String getName();    
    
    public void addParameter(ValueHandler valueHandler);
    
    public ValueHandler[] getParameters();
    
    public void replaceTempTokens(Map variables);
            
}
