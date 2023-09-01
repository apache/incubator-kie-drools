package org.kie.internal.runtime.error;

import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.task.model.Task;

public class ExecutionErrorContext {

    private Throwable cause;
    
    private NodeInstance firstExecutedNode;
    
    private NodeInstance lastExecutedNode;
    
    private Task lastExecutedTask;
    
    public ExecutionErrorContext(Throwable cause, NodeInstance lastExecutedNode, Task lastExecutedTask, NodeInstance firstExecutedNode) {
        super();
        this.cause = cause;
        this.lastExecutedNode = lastExecutedNode;
        this.lastExecutedTask = lastExecutedTask;
        this.firstExecutedNode = firstExecutedNode;
    }

    public Throwable getCause() {
        return cause;
    }
    
    public NodeInstance getLastExecutedNode() {
        return lastExecutedNode;
    }
    
    public Task getLastExecutedTask() {
        return lastExecutedTask;
    }    
    
    public NodeInstance getFirstExecutedNode() {
        return firstExecutedNode;
    }

    @Override
    public String toString() {
        return "ExecutionErrorContext [cause=" + cause + ", firstExecutedNode=" + firstExecutedNode +
                ", lastExecutedNode=" + lastExecutedNode + ", lastExecutedTask=" + lastExecutedTask + "]";
    }
    
}
