package org.drools.clips;

import java.util.Map;

public interface MVELBuildContext {
    Map<String, String> getVariableNameMap();
    
    String makeValid(String var);
}
