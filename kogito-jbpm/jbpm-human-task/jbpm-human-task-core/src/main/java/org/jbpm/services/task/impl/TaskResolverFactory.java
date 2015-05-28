package org.jbpm.services.task.impl;

import org.kie.api.task.model.Task;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;


public class TaskResolverFactory extends ImmutableDefaultFactory {

    private static final long serialVersionUID = 8019024969834990593L;
    private Task task;
    
    public TaskResolverFactory(Task task) {
        this.task = task;              
    }
    public boolean isResolveable(String name) {
        return "task".equals(name);
    }
    
    
    public VariableResolver getVariableResolver(String name) {
   
        return new SimpleValueResolver(task);
    }

}
