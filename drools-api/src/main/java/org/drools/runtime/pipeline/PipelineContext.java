package org.drools.runtime.pipeline;

import java.util.Map;

public interface PipelineContext {
        
    ClassLoader getClassLoader();
    
    Map<String, Object> getProperties();
    
    void setResult(Object result);
    
    Object getResult();
    
    ResultHandler getResultHandler();
    
}
