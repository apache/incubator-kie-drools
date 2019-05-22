package org.jbpm.process.codegen;

import java.util.Map;
import java.util.HashMap;

public class XXXModel implements org.kie.submarine.Model {
    
    private Long id;
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return this.id;
    }
    
    @Override
    public Map<String, Object> toMap() {
        
    }
    
    @Override
    public void fromMap(Map<String, Object> params) {
        fromMap(null, params);
    }

    public void fromMap(Long id, Map<String, Object> params) {
        
    }
}