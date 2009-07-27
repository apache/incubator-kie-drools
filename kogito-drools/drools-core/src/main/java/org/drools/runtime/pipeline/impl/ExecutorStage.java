package org.drools.runtime.pipeline.impl;

import java.util.Collection;
import java.util.List;

import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.result.ExecutionResults;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;

public class ExecutorStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {

    public void receive(Object object,
                        PipelineContext context) {
        BasePipelineContext kContext = (BasePipelineContext) context;
        if ( object instanceof Collection ) {
            object = CommandFactory.newBatchExecution( (List<Command<?>>) object );
        }

        ExecutionResults result = kContext.getCommandExecutor().execute( (Command<?>) object );

        emit( result,
              kContext );
    }

}
