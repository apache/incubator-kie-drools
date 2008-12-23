package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.PipelineContext;

public class BasePipelineContext implements PipelineContext {
    private ClassLoader               rootClassLoader;

    public BasePipelineContext(ClassLoader               rootClassLoader) {
        this.rootClassLoader = rootClassLoader;
    }

    public ClassLoader getClassLoader() {
        return this.rootClassLoader;
    }
}
