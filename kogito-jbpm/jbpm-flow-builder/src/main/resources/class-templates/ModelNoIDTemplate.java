package org.jbpm.process.codegen;

import java.util.Map;
import java.util.HashMap;

public class XXXModel implements org.kie.kogito.Model {
    
    @Override
    public Map<String, Object> toMap() {
        
    }
    
    @Override
    public void fromMap(Map<String, Object> params) {
        fromMap(null, params);
    }

    @Override
    public void update(Map<String, Object> params) {
        fromMap(params);
    }

    public void fromMap(String id, Map<String, Object> params) {
        
    }
}