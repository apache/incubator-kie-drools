package org.drools.process.core.context.variable;

import java.util.Map;

public interface Mappable {

    void addInMapping(String parameterName, String variableName);
    void setInMappings(Map<String, String> inMapping);
    String getInMapping(String parameterName);
    Map<String, String> getInMappings();
    
    void addOutMapping(String parameterName, String variableName);
    void setOutMappings(Map<String, String> outMapping);
    String getOutMapping(String parameterName);
    Map<String, String> getOutMappings();

}
