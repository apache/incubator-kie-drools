package org.drools.runtime.pipeline.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.CommandExecutor;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.ResultHandler;

public class BasePipelineContext
    implements
    PipelineContext {
    private ClassLoader         classLoader;
    private Map<String, Object> properties;
    private Object              result;
    private ResultHandler       resultHandler;

    public BasePipelineContext(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public BasePipelineContext(ClassLoader classLoader,
                               ResultHandler resultHandler) {
        this.classLoader = classLoader;
        this.resultHandler = resultHandler;
        this.properties = new HashMap<String, Object>();
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public ResultHandler getResultHandler() {
        return this.resultHandler;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public CommandExecutor getCommandExecutor() {
        throw new UnsupportedOperationException( "this method is not implemented" );
    }
        
}
