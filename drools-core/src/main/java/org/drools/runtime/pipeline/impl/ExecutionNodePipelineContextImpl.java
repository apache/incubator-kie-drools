package org.drools.runtime.pipeline.impl;

import org.drools.grid.ExecutionNode;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.pipeline.impl.BasePipelineContext;

public class ExecutionNodePipelineContextImpl extends BasePipelineContext {

    private ExecutionNode node;
    private CommandExecutor commandExecutor;

    public ExecutionNodePipelineContextImpl(ExecutionNode node,
                                             ClassLoader classLoader) {
        this( node, classLoader, null );
    }

    public ExecutionNodePipelineContextImpl(ExecutionNode node,
                                             ClassLoader classLoader,
                                             ResultHandler resultHandler) {
        super( classLoader, resultHandler );
        this.node = node;
    }

    public ExecutionNode getExecutionNode() {
        return this.node;
    }

    public CommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

}
