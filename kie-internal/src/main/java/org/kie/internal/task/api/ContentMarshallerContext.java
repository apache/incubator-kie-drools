package org.kie.internal.task.api;

import org.kie.api.runtime.Environment;

public class ContentMarshallerContext {

    private Environment environment;
    private ClassLoader classloader;
    
    public ContentMarshallerContext() {
        
    }
    
    public ContentMarshallerContext(Environment environment, ClassLoader classloader) {
        this.environment = environment;
        this.classloader = classloader;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    public ClassLoader getClassloader() {
        return classloader;
    }
    public void setClassloader(ClassLoader classloader) {
        this.classloader = classloader;
    }
    
    
}
