package org.drools.runtime.pipeline.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.pipeline.PipelineContext;

public class BasePipelineContext implements PipelineContext {
    private ClassLoader               classLoader;
    private Map<String, Object>       properties;

    public BasePipelineContext(ClassLoader               classLoader) {
        this.classLoader = classLoader;
        this.properties = new HashMap<String, Object>();
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
        
}
