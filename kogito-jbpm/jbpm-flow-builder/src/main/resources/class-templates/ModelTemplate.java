package org.jbpm.process.codegen;

import java.util.Map;
import java.util.HashMap;
import javax.validation.constraints.NotNull;

public class XXXModel {

    @NotNull
    private Long id;
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public Map<String, Object> toMap() {
        
    }

    public void fromMap(Long id, Map<String, Object> params) {
        
    }
}