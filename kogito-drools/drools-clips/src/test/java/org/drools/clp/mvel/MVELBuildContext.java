package org.drools.clp.mvel;

import java.util.Map;

public interface MVELBuildContext {
    Map<String, String> getVariableNameMap();
    
    String makeValid(String var);
}
