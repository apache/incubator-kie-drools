package org.kie.internal.runtime.error;

import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.task.model.Task;

public interface ExecutionErrorHandler {

    
    void processing(NodeInstance nodeInstance);
    
    void processing(Task task);
    
    void processed(NodeInstance nodeInstance);
    
    void processed(Task task);
    
    void handle(Throwable cause);
}
