package org.drools.runtime.pipeline;

import java.util.Map;

public interface PipelineContext {
    ClassLoader getClassLoader();
    
    public Map<String, Object> getProperties();
}
